package laiss.pokemon.pokemonandroidoldtech.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    init {
        _state.value = State.Loading
        pokemonRepository.getPokemonByName(pokemonName, ::onPokemonLoaded)
    }

    private fun onPokemonLoaded(result: Result<Pokemon>) {
        try {
            _pokemon.value = result.getOrThrow()
            _state.value = State.Presenting
        } catch (exception: Exception) {
            _lastError.value = exception.message
            _state.value = State.Error
        }
    }
}