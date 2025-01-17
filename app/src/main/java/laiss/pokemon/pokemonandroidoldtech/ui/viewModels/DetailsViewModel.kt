package laiss.pokemon.pokemonandroidoldtech.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import laiss.pokemon.pokemonandroidoldtech.data.IPokemonRepository
import laiss.pokemon.pokemonandroidoldtech.data.models.Pokemon
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DetailsViewModel(pokemonName: String) : ViewModel(), KoinComponent {
    private val pokemonRepository: IPokemonRepository by inject()

    private val _pokemon = MutableStateFlow<Pokemon?>(null)
    val pokemon: StateFlow<Pokemon?> = _pokemon

    enum class State { Loading, Error, Presenting }

    private val _state = MutableStateFlow(State.Loading)
    val state: StateFlow<State> = _state

    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError

    class Factory(private val pokemonName: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            DetailsViewModel(pokemonName) as T
    }

    init {
        viewModelScope.launch {
            try {
                _pokemon.value = pokemonRepository.getPokemonByName(pokemonName)
                _state.value = State.Presenting
            } catch (exception: Exception) {
                _lastError.value = exception.message
                _state.value = State.Error
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}