package laiss.pokemon.pokemonandroidoldtech.ui.viewModels

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import laiss.pokemon.pokemonandroidoldtech.data.IPokemonRepository
import laiss.pokemon.pokemonandroidoldtech.data.models.Pokemon
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.concurrent.thread


private const val MIN_ON_PAGE = 30

class OverviewViewModel : ViewModel(), KoinComponent {
    private val pokemonRepository: IPokemonRepository by inject()
    private val uiHandler: Handler by inject()

    private var isEndReached = false
    private var page = 0
    private var pagingOffset = 0
    private var isRefreshRequested = false

    private val _pokemonList = MutableLiveData(emptyList<Pokemon>())
    val pokemonList: LiveData<List<Pokemon>> = _pokemonList

    private val _state = MutableLiveData(State.Loading)
    val state: LiveData<State> = _state

    private val _lastError = MutableLiveData<String?>(null)
    val lastError: LiveData<String?> = _lastError

    private val _isAttackSortChecked = MutableLiveData(false)
    val isAttackSortChecked: LiveData<Boolean> = _isAttackSortChecked

    private val _isDefenseSortChecked = MutableLiveData(false)
    val isDefenseSortChecked: LiveData<Boolean> = _isDefenseSortChecked

    private val _isHpSortChecked = MutableLiveData(false)
    val isHpSortChecked: LiveData<Boolean> = _isHpSortChecked

    var onScrollToTopRequested: () -> Unit = {}

    init {
        pokemonRepository.getPage(0, 0, ::onPageLoaded)
    }

    enum class State { Loading, Error, Presenting, LoadingAdditionalPage }

    fun startLoadingNextPage() {
        if (isEndReached || state.value != State.Presenting) return
        _state.value = State.LoadingAdditionalPage
        pokemonRepository.getPage(page + 1, pagingOffset, ::onPageLoaded)
    }

    fun refreshFromRandomPlace() {
        if (isRefreshRequested) return
        isRefreshRequested = true

        if (state.value == State.Loading) {
            waitForLoadingComplete(::refreshFromRandomPlace)
            return
        }
        _state.value = State.Loading
        _pokemonList.value = emptyList()
        pokemonRepository.getRandomPageNumberAndOffset(::onRandomNumberAndOffsetReceived)
    }

    fun onAttackSortChecked(isChecked: Boolean) {
        _isAttackSortChecked.value = isChecked
        if (isChecked.not()) return

        _pokemonList.value = pokemonList.value!!.sortedByDescending { it.attack }
        onScrollToTopRequested()
    }

    fun onDefenseSortChecked(isChecked: Boolean) {
        _isDefenseSortChecked.value = isChecked
        if (isChecked.not()) return

        _pokemonList.value = pokemonList.value!!.sortedByDescending { it.defense }
        onScrollToTopRequested()
    }

    fun onHpSortChecked(isChecked: Boolean) {
        _isHpSortChecked.value = isChecked
        if (isChecked.not()) return

        _pokemonList.value = pokemonList.value!!.sortedByDescending { it.hp }
        onScrollToTopRequested()
    }

    private fun onPageLoaded(newPageResult: Result<List<Pokemon>>) {
        try {
            val newPage = newPageResult.getOrThrow()
            if (newPage.isEmpty())
                isEndReached = true
            _pokemonList.value = pokemonList.value!! + newPage
            dropSorts()
            _state.value = State.Presenting
            ++page
        } catch (exception: Exception) {
            _lastError.value = exception.message
            _state.value = State.Error
        }
    }

    private fun onRandomNumberAndOffsetReceived(result: Result<Pair<Int, Int>>) = try {
        val (newPage, newPagingOffset) = result.getOrThrow()
        pokemonRepository.getPage(newPage, newPagingOffset, ::onRandomPageLoaded)
        page = newPage
        pagingOffset = newPagingOffset

    } catch (exception: Exception) {
        _lastError.value = exception.message
        _state.value = State.Error
    }

    private fun onRandomPageLoaded(result: Result<List<Pokemon>>) {
        try {
            val entries = result.getOrThrow()
            _pokemonList.value = entries
            if (entries.size < MIN_ON_PAGE) pokemonRepository.getPage(0, 0, ::onPageLoaded)
            dropSorts()
            _state.value = State.Presenting
        } catch (exception: Exception) {
            _lastError.value = exception.message
            _state.value = State.Error
        } finally {
            isRefreshRequested = false
        }
    }

    private fun waitForLoadingComplete(callback: () -> Unit) {
        thread {
            while (state.value == State.Loading)
                Thread.sleep(100)
            uiHandler.post { callback() }
        }
    }

    private fun dropSorts() {
        _isAttackSortChecked.value = false
        _isDefenseSortChecked.value = false
        _isHpSortChecked.value = false
    }
}