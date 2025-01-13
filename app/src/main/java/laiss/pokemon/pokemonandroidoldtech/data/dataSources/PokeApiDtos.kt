package laiss.pokemon.pokemonandroidoldtech.data.dataSources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonHeaderDto(val name: String)

@Serializable
data class PokemonHeadersListDto(val count: Int, val results: List<PokemonHeaderDto>)

@Serializable
data class PokemonSpritesDto(@SerialName("front_default") val frontDefault: String?)

@Serializable
data class PokemonTypeDto(val name: String)

@Serializable
data class PokemonTypesDto(val type: PokemonTypeDto)

@Serializable
data class PokemonStatDto(val name: String)

@Serializable
data class PokemonStatsDto(@SerialName("base_stat") val baseStat: Int, val stat: PokemonStatDto)

@Serializable
data class PokemonDto(
    val name: String,
    val id: Int,
    val height: Int,
    val weight: Int,
    val sprites: PokemonSpritesDto,
    val types: List<PokemonTypesDto>,
    val stats: List<PokemonStatsDto>
)