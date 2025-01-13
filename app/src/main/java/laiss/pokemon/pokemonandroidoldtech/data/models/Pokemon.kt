package laiss.pokemon.pokemonandroidoldtech.data.models

import laiss.pokemon.pokemonandroidoldtech.data.dataSources.PokemonDto
import laiss.pokemon.pokemonandroidoldtech.data.dataSources.PokemonEntity

enum class PokemonType(val typeString: String) {
    Normal("normal"),
    Fire("fire"),
    Fighting("fighting"),
    Water("water"),
    Flying("flying"),
    Grass("grass"),
    Poison("poison"),
    Electric("electric"),
    Ground("ground"),
    Psychic("psychic"),
    Rock("rock"),
    Ice("ice"),
    Bug("bug"),
    Dragon("dragon"),
    Ghost("ghost"),
    Dark("dark"),
    Steel("steel"),
    Fairy("fairy"),
    Stellar("stellar"),
    Unknown("???");

    companion object {
        operator fun get(typeString: String) = entries.firstOrNull { it.typeString == typeString }
    }
}

class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val heightDm: Int,
    val weightHg: Int,
    val types: List<PokemonType>,
    val attack: Int,
    val defense: Int,
    val hp: Int
)

fun PokemonDto.toModel() = Pokemon(
    id = id,
    name = name,
    imageUrl = sprites.frontDefault,
    heightDm = height,
    weightHg = weight,
    types = types.map { PokemonType[it.type.name] ?: PokemonType.Unknown },
    attack = stats.first { it.stat.name == "attack" }.baseStat,
    defense = stats.first { it.stat.name == "defense" }.baseStat,
    hp = stats.first { it.stat.name == "hp" }.baseStat
)

fun PokemonEntity.toModel() = Pokemon(
    id = id,
    name = name,
    imageUrl = imageUrl,
    heightDm = height,
    weightHg = weight,
    types = types.split(" ").map { PokemonType[it] ?: PokemonType.Unknown },
    attack = attack,
    defense = defense,
    hp = hp
)

fun Pokemon.toEntity() = PokemonEntity(
    id = id,
    name = name,
    imageUrl = imageUrl,
    height = heightDm,
    weight = weightHg,
    types = types.joinToString(" ") { it.typeString },
    attack = attack,
    defense = defense,
    hp = hp
)