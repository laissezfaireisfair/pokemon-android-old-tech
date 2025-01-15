package laiss.pokemon.pokemonandroidoldtech.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import laiss.pokemon.pokemonandroidoldtech.data.IPokemonRepository
import laiss.pokemon.pokemonandroidoldtech.data.models.Pokemon
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class OverviewViewModel : ViewModel(), KoinComponent {
    private val pokemonRepository: IPokemonRepository by inject()

    private val _pokemonList: MutableLiveData<List<Pokemon>> =
        MutableLiveData<List<Pokemon>>()
    val pokemonList: LiveData<List<Pokemon>> = _pokemonList

    init {
        pokemonRepository.getPage(0, 0, ::refreshPokemonList)
    }

    fun refreshPokemonList(newPokemonList: List<Pokemon>) {
        _pokemonList.value = newPokemonList
    }
}