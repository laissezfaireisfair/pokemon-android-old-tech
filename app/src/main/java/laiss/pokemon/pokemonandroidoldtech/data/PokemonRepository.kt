package laiss.pokemon.pokemonandroidoldtech.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import laiss.pokemon.pokemonandroidoldtech.data.dataSources.LocalStorageDataSource
import laiss.pokemon.pokemonandroidoldtech.data.dataSources.PokeApiDataSource
import laiss.pokemon.pokemonandroidoldtech.data.dataSources.PokemonEntity
import laiss.pokemon.pokemonandroidoldtech.data.models.Pokemon
import laiss.pokemon.pokemonandroidoldtech.data.models.toEntity
import laiss.pokemon.pokemonandroidoldtech.data.models.toModel
import kotlin.random.Random

interface IPokemonRepository {
    suspend fun getPage(number: Int, pagingOffset: Int = 0): Flow<Pokemon>

    suspend fun getRandomPageNumberAndOffset(): Pair<Int, Int>

    suspend fun getPokemonByName(pokemonName: String): Pokemon
}

class PokemonRepository(
    private val pokeApiDataSource: PokeApiDataSource,
    private val localStorageDataSource: LocalStorageDataSource,
    private val pageSize: Int
) : IPokemonRepository {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val strategy by lazy {
        scope.async {
            val pokemonEntities = localStorageDataSource.getPokemonList()
            try {
                val pokemonCount = pokeApiDataSource.getPokemonHeadersList(0, 1).count
                return@async OnlineStrategy(pokemonCount, pokemonEntities)
            } catch (_: Exception) {
                return@async OfflineStrategy(pokemonEntities)
            }
        }
    }

    override suspend fun getPage(number: Int, pagingOffset: Int): Flow<Pokemon> {
        return strategy.await().getPage(number, pagingOffset)
    }

    override suspend fun getRandomPageNumberAndOffset(): Pair<Int, Int> {
        val pageNumber = Random.nextInt(0, strategy.await().pokemonCount / pageSize)
        val pagingOffset = Random.nextInt(0, pageSize)
        return pageNumber to pagingOffset
    }

    override suspend fun getPokemonByName(pokemonName: String): Pokemon {
        return strategy.await().getPokemonByName(pokemonName)
    }

    private interface IStrategy {
        suspend fun getPage(number: Int, pagingOffset: Int): Flow<Pokemon>
        suspend fun getPokemonByName(pokemonName: String): Pokemon
        val pokemonCount: Int
    }

    private inner class OnlineStrategy(
        override val pokemonCount: Int,
        pokemonEntities: List<PokemonEntity>,
    ) : IStrategy {
        /**
         * Copies remote structure, nulls for non-loaded items*/
        private val pokemonListCache = MutableList<Pokemon?>(pokemonCount) { null }
        private val pokemonByNameCache =
            pokemonEntities.map { it.toModel() }.associateBy { it.name }.toMutableMap()

        override suspend fun getPage(number: Int, pagingOffset: Int): Flow<Pokemon> {
            val offset = pageSize * number + pagingOffset
            if (pokemonCount <= offset) return emptyFlow()

            val indices = (offset..<(offset + pageSize))
            if (indices.all { pokemonListCache[it] != null })
                return flow { indices.forEach { emit(pokemonListCache[it]!!) } }

            val headerList = pokeApiDataSource.getPokemonHeadersList(offset, pageSize)

            return flow {
                coroutineScope {
                    headerList.results.map { async { getPokemonByName(it.name) } }
                        .forEachIndexed { i, pokemonDeferred ->
                            val pokemon = pokemonDeferred.await()
                            pokemonListCache[offset + i] = pokemon
                            emit(pokemon)
                        }
                }
            }
        }

        override suspend fun getPokemonByName(pokemonName: String): Pokemon {
            val cachedPokemon = pokemonByNameCache[pokemonName]
            if (cachedPokemon != null) return cachedPokemon

            val pokemon = pokeApiDataSource.getPokemon(pokemonName).toModel()
            pokemonByNameCache[pokemon.name] = pokemon
            localStorageDataSource.storePokemon(pokemon.toEntity())
            return pokemon
        }
    }

    private inner class OfflineStrategy(pokemonEntities: List<PokemonEntity>) : IStrategy {
        override val pokemonCount: Int
            get() = pokemonList.size
        private val pokemonList = pokemonEntities.map { it.toModel() }
        private val pokemonByName = pokemonList.associateBy { it.name }

        override suspend fun getPage(number: Int, pagingOffset: Int): Flow<Pokemon> {
            val offset = pageSize * number + pagingOffset
            if (pokemonCount <= offset) return emptyFlow()
            return pokemonList.listIterator(offset).asFlow()
                .take(offset + pageSize)
        }

        override suspend fun getPokemonByName(pokemonName: String) = pokemonByName[pokemonName]
            ?: throw IllegalArgumentException("No pokemon with such name")
    }
}