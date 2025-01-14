package laiss.pokemon.pokemonandroidoldtech.utils

fun String.capitalize() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }