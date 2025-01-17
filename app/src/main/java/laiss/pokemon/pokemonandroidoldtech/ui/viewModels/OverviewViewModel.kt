package laiss.pokemon.pokemonandroidoldtech.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import laiss.pokemon.pokemonandroidoldtech.data.IPokemonRepository
import laiss.pokemon.pokemonandroidoldtech.data.models.Pokemon
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val MIN_ON_PAGE = 30

class OverviewViewModel : ViewModel(), KoinComponent {
    private val pokemonRepository: IPokemonRepository by inject()

    private var isEndReached = false
    private var page = 0
    private var pagingOffset = 0
    private var isRefreshRequested = false

    private val _pokemonList = MutableStateFlow(emptyList<Pokemon>())
    val pokemonList: StateFlow<List<Pokemon>> = _pokemonList

    private val _state = MutableStateFlow(State.Loading)
    val state: StateFlow<State> = _state

    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError

    private val _isAttackSortChecked = MutableStateFlow(false)
    val isAttackSorting: StateFlow<Boolean> = _isAttackSortChecked

    private val _isDefenseSortChecked = MutableStateFlow(false)
    val isDefenseSortChecked: StateFlow<Boolean> = _isDefenseSortChecked

    private val _isHpSortChecked = MutableStateFlow(false)
    val isHpSortChecked: StateFlow<Boolean> = _isHpSortChecked

    var onScrollToTopRequested: () -> Unit = {}

    init {
        viewModelScope.launch {
            try {
                val entries = pokemonRepository.getPage(0)
                _pokemonList.value = entries
                _state.value = State.Presenting
            } catch (exception: Exception) {
                _lastError.value = exception.message
                _state.value = State.Error
            }
        }
    }

    enum class State { Loading, Error, Presenting, LoadingAdditionalPage }

    fun loadNextPage() {
        viewModelScope.launch {
            try {
                if (isEndReached || state.value != State.Presenting) return@launch
                _state.value = State.LoadingAdditionalPage

                val newPage = pokemonRepository.getPage(page + 1, pagingOffset)
                if (newPage.isEmpty())
                    isEndReached = true
                _pokemonList.value = pokemonList.value + newPage
                dropSorts()
                _state.value = State.Presenting
                ++page
            } catch (exception: Exception) {
                _lastError.value = exception.message
                _state.value = State.Error
            }
        }
    }

    fun refreshFromRandomPlace() {
        viewModelScope.launch {
            try {
                if (isRefreshRequested) return@launch
                isRefreshRequested = true

                while (state.value == State.Loading) delay(100)
                _state.value = State.Loading
                _pokemonList.value = emptyList()

                val (newPage, newPagingOffset) = pokemonRepository.getRandomPageNumberAndOffset()

                val entries = pokemonRepository.getPage(newPage, newPagingOffset).toMutableList()
                if (entries.size < MIN_ON_PAGE) entries += pokemonRepository.getPage(0)
                _pokemonList.value = entries.toList()

                dropSorts()
                _state.value = State.Presenting

                page = newPage
                pagingOffset = newPagingOffset
            } catch (exception: Exception) {
                _lastError.value = exception.message
                _state.value = State.Error
            } finally {
                isRefreshRequested = false
            }
        }
    }

    fun onAttackSortChecked(isChecked: Boolean) {
        _isAttackSortChecked.value = isChecked
        if (isChecked.not()) return

        _pokemonList.value = pokemonList.value.sortedByDescending { it.attack }
        onScrollToTopRequested()
    }

    fun onDefenseSortChecked(isChecked: Boolean) {
        _isDefenseSortChecked.value = isChecked
        if (isChecked.not()) return

        _pokemonList.value = pokemonList.value.sortedByDescending { it.defense }
        onScrollToTopRequested()
    }

    fun onHpSortChecked(isChecked: Boolean) {
        _isHpSortChecked.value = isChecked
        if (isChecked.not()) return

        _pokemonList.value = pokemonList.value.sortedByDescending { it.hp }
        onScrollToTopRequested()
    }

    private fun dropSorts() {
        _isAttackSortChecked.value = false
        _isDefenseSortChecked.value = false
        _isHpSortChecked.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}