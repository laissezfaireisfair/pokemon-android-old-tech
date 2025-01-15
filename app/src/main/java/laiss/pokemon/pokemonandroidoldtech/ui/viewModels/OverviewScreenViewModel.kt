package laiss.pokemon.pokemonandroidoldtech.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import laiss.pokemon.pokemonandroidoldtech.data.IPokemonRepository
import laiss.pokemon.pokemonandroidoldtech.data.models.Pokemon
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import laiss.pokemon.pokemonandroidoldtech.utils.capitalize

data class OverviewScreenEntry(
    val name: String,
    val imageUrl: String?,
    val attack: Int,
    val defense: Int,
    val hp: Int
)

fun Pokemon.toEntry() = OverviewScreenEntry(
    name = name.capitalize(),
    imageUrl = imageUrl,
    attack = attack,
    defense = defense,
    hp = hp
)

class OverviewScreenViewModel : ViewModel(), KoinComponent {
    private val pokemonRepository: IPokemonRepository by inject()

    private val _entries: MutableLiveData<List<OverviewScreenEntry>> =
        MutableLiveData<List<OverviewScreenEntry>>()
    val entries: LiveData<List<OverviewScreenEntry>> = _entries

    init {
        _entries.value = listOf(
            OverviewScreenEntry(
                name = "name",
                imageUrl = "",
                attack = 1,
                defense = 1,
                hp = 1
            )
        )
    }
}