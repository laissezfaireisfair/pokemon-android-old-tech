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

    private var isEndReached = false
    private var page = 0
    private var pagingOffset = 0

    private val _pokemonList = MutableLiveData<List<Pokemon>>()
    val pokemonList: LiveData<List<Pokemon>> = _pokemonList

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    init {
        pokemonRepository.getPage(0, 0, ::refreshPokemonList)
    }

    fun startLoadingNextPage() {
        if (isEndReached || isLoading.value!!) return
        _isLoading.value = true
        pokemonRepository.getPage(page + 1, pagingOffset, ::appendPage)
    }

    private fun refreshPokemonList(newPokemonList: List<Pokemon>) {
        _pokemonList.value = newPokemonList
    }

    private fun appendPage(newPage: List<Pokemon>) {
        _isLoading.value = false
        _pokemonList.value = pokemonList.value!! + newPage
        ++page
    }
}