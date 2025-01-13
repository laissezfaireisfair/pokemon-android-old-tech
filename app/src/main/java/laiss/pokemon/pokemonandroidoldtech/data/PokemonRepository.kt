package laiss.pokemon.pokemonandroidoldtech.data

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import laiss.pokemon.pokemonandroidoldtech.data.dataSources.LocalStorageDataSource
import laiss.pokemon.pokemonandroidoldtech.data.dataSources.PokeApiDataSource
import laiss.pokemon.pokemonandroidoldtech.data.dataSources.PokemonEntity
import laiss.pokemon.pokemonandroidoldtech.data.models.Pokemon
import laiss.pokemon.pokemonandroidoldtech.data.models.toEntity
import laiss.pokemon.pokemonandroidoldtech.data.models.toModel
import kotlin.random.Random

interface IPokemonRepository {
    fun getPage(number: Int, pagingOffset: Int = 0): List<Pokemon>

    fun getRandomPageNumberAndOffset(): Pair<Int, Int>

    fun getPokemonByName(pokemonName: String): Pokemon
}


class PokemonRepository(
    internal val pokeApiDataSource: PokeApiDataSource,
    internal val localStorageDataSource: LocalStorageDataSource,
    internal val pageSize: Int
) : IPokemonRepository {
    private lateinit var strategy: IStrategy
    private var isInitialized = false

    private fun ensureIsInitialized() {
        if (isInitialized) return

        val pokemonEntities = localStorageDataSource.getPokemonList()
        try {
            val pokemonCount = pokeApiDataSource.getPokemonHeadersList(0, 1).count
            strategy = OnlineStrategy(pokemonCount, pokemonEntities, this)
        } catch (_: Exception) {
            strategy = OfflineStrategy(pokemonEntities, this)
        }

        isInitialized = true
    }

    override fun getPage(number: Int, pagingOffset: Int): List<Pokemon> {
        ensureIsInitialized()
        return strategy.getPage(number, pagingOffset)
    }

    override fun getRandomPageNumberAndOffset(): Pair<Int, Int> {
        ensureIsInitialized()
        val pageNumber = Random.nextInt(0, strategy.pokemonCount / pageSize)
        val pagingOffset = Random.nextInt(0, pageSize)
        return pageNumber to pagingOffset
    }

    override fun getPokemonByName(pokemonName: String): Pokemon {
        ensureIsInitialized()
        return strategy.getPokemonByName(pokemonName)
    }
}

private interface IStrategy {
    fun getPage(number: Int, pagingOffset: Int): List<Pokemon>
    fun getPokemonByName(pokemonName: String): Pokemon
    val pokemonCount: Int
}

private class OnlineStrategy(
    override val pokemonCount: Int,
    pokemonEntities: List<PokemonEntity>,
    private val repository: PokemonRepository
) : IStrategy {
    /**
     * Copies remote structure, nulls for non-loaded items*/
    private val pokemonListCache = MutableList<Pokemon?>(pokemonCount) { null }
    private val pokemonByNameCache =
        pokemonEntities.map { it.toModel() }.associateBy { it.name }.toMutableMap()

    override fun getPage(number: Int, pagingOffset: Int): List<Pokemon> {
        val offset = repository.pageSize * number + pagingOffset
        if (pokemonCount <= offset) return emptyList()

        val indices = (offset..<(offset + repository.pageSize))
        if (indices.all { pokemonListCache[it] != null })
            return pokemonListCache.listIterator(offset).asSequence()
                .take(repository.pageSize)
                .map { it!! }.toList()

        val headerList =
            repository.pokeApiDataSource.getPokemonHeadersList(offset, repository.pageSize)

        val pokemonList = headerList.results.map { getPokemonByName(it.name) }
        pokemonList.forEachIndexed { i, pokemon -> pokemonListCache[offset + i] = pokemon }
        return pokemonList
    }

    override fun getPokemonByName(pokemonName: String): Pokemon {
        val cachedPokemon = pokemonByNameCache[pokemonName]
        if (cachedPokemon != null) return cachedPokemon

        val pokemon = repository.pokeApiDataSource.getPokemon(pokemonName).toModel()
        pokemonByNameCache[pokemon.name] = pokemon
        repository.localStorageDataSource.storePokemon(pokemon.toEntity())
        return pokemon
    }
}

private class OfflineStrategy(
    pokemonEntities: List<PokemonEntity>,
    private val repository: PokemonRepository
) : IStrategy {
    override val pokemonCount: Int
        get() = pokemonList.size
    private val pokemonList = pokemonEntities.map { it.toModel() }
    private val pokemonByName = pokemonList.associateBy { it.name }

    override fun getPage(number: Int, pagingOffset: Int): List<Pokemon> {
        val offset = repository.pageSize * number + pagingOffset
        if (pokemonCount <= offset) return emptyList()
        return pokemonList.listIterator(offset).asSequence()
            .take(offset + repository.pageSize).toList()
    }

    override fun getPokemonByName(pokemonName: String) = pokemonByName[pokemonName]
        ?: throw IllegalArgumentException("No pokemon with such name")
}