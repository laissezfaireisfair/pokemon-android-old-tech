package laiss.pokemon.pokemonandroidoldtech.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import laiss.pokemon.pokemonandroidoldtech.data.IPokemonRepository
import laiss.pokemon.pokemonandroidoldtech.data.models.Pokemon
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DetailsViewModel(pokemonName: String) : ViewModel(), KoinComponent {
    private val pokemonRepository: IPokemonRepository by inject()

    private val _pokemon = MutableLiveData<Pokemon?>()
    val pokemon: LiveData<Pokemon?> = _pokemon

    enum class State { Loading, Error, Presenting }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private val _lastError = MutableLiveData<String?>()
    val lastError: LiveData<String?> = _lastError

    class Factory(private val pokemonName: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            DetailsViewModel(pokemonName) as T
    }

    init {
        viewModelScope.launch {
            try {
                _state.value = State.Loading
                _pokemon.value = pokemonRepository.getPokemonByName(pokemonName)
                _state.value = State.Presenting
            } catch (exception: Exception) {
                _lastError.value = exception.message
                _state.value = State.Error
            }
        }
    }
}