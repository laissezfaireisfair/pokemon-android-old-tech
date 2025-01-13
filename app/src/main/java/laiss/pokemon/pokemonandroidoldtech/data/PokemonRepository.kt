package laiss.pokemon.pokemonandroidoldtech.data

import laiss.pokemon.pokemonandroidoldtech.data.models.Pokemon

interface IPokemonRepository {
    suspend fun getPage(number: Int, pagingOffset: Int = 0): List<Pokemon>

    suspend fun getRandomPageNumberAndOffset(): Pair<Int, Int>

    suspend fun getPokemonByName(pokemonName: String): Pokemon
}