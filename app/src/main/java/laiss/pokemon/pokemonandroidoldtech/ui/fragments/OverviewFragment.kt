package laiss.pokemon.pokemonandroidoldtech.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import laiss.pokemon.pokemonandroidoldtech.R
import laiss.pokemon.pokemonandroidoldtech.data.models.Pokemon
import laiss.pokemon.pokemonandroidoldtech.databinding.OverviewFragmentBinding
import laiss.pokemon.pokemonandroidoldtech.ui.adapters.PokemonAdapter
import laiss.pokemon.pokemonandroidoldtech.ui.viewModels.OverviewViewModel

class OverviewFragment : Fragment() {
    private lateinit var binding: OverviewFragmentBinding
    private val viewModel: OverviewViewModel by viewModels()

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = OverviewFragmentBinding.inflate(inflater, container, false)

        scope.launch {
            viewModel.state.collect {
                when (it) {
                    OverviewViewModel.State.Loading -> {
                        with(binding) {
                            errorHeader.visibility = View.GONE
                            errorMessage.visibility = View.GONE
                            pokemonRv.visibility = View.INVISIBLE
                            loadingIndicator.visibility = View.VISIBLE
                            loadingNextPageIndicator.visibility = View.GONE
                            attackSortCheckbox.visibility = View.INVISIBLE
                            defenseSortCheckbox.visibility = View.INVISIBLE
                            hpSortCheckbox.visibility = View.INVISIBLE
                            sortDivider.visibility = View.INVISIBLE
                        }
                    }

                    OverviewViewModel.State.Error -> {
                        with(binding) {
                            errorHeader.visibility = View.VISIBLE
                            errorMessage.visibility = View.VISIBLE
                            pokemonRv.visibility = View.GONE
                            loadingIndicator.visibility = View.GONE
                            loadingNextPageIndicator.visibility = View.GONE
                            attackSortCheckbox.visibility = View.GONE
                            defenseSortCheckbox.visibility = View.GONE
                            hpSortCheckbox.visibility = View.GONE
                            sortDivider.visibility = View.GONE
                        }
                    }

                    OverviewViewModel.State.Presenting -> {
                        with(binding) {
                            errorHeader.visibility = View.GONE
                            errorMessage.visibility = View.GONE
                            pokemonRv.visibility = View.VISIBLE
                            loadingIndicator.visibility = View.GONE
                            loadingNextPageIndicator.visibility = View.GONE
                            attackSortCheckbox.visibility = View.VISIBLE
                            defenseSortCheckbox.visibility = View.VISIBLE
                            hpSortCheckbox.visibility = View.VISIBLE
                            sortDivider.visibility = View.VISIBLE
                        }
                    }

                    OverviewViewModel.State.LoadingAdditionalPage -> {
                        with(binding) {
                            errorHeader.visibility = View.GONE
                            pokemonRv.visibility = View.VISIBLE
                            errorMessage.visibility = View.GONE
                            loadingIndicator.visibility = View.GONE
                            loadingNextPageIndicator.visibility = View.VISIBLE
                            attackSortCheckbox.visibility = View.VISIBLE
                            defenseSortCheckbox.visibility = View.VISIBLE
                            hpSortCheckbox.visibility = View.VISIBLE
                            sortDivider.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        scope.launch { viewModel.lastError.collect { binding.errorMessage.text = it } }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.pokemonRv.layoutManager = layoutManager

        val pokemonAdapter = PokemonAdapter(requireContext(), ::onPokemonClicked)
        binding.pokemonRv.adapter = pokemonAdapter

        scope.launch { viewModel.pokemonList.collect { pokemonAdapter.submitList(it) } }

        binding.pokemonRv.addOnScrollListener(
            EndReachListener(layoutManager, viewModel::loadNextPage)
        )

        scope.launch {
            viewModel.isAttackSorting.collect { binding.attackSortCheckbox.isChecked = it }
        }

        scope.launch {
            viewModel.isDefenseSortChecked.collect { binding.defenseSortCheckbox.isChecked = it }
        }

        scope.launch {
            viewModel.isHpSortChecked.collect { binding.hpSortCheckbox.isChecked = it }
        }

        with(binding) {
            fab.setOnClickListener { viewModel.refreshFromRandomPlace() }

            attackSortCheckbox.setOnCheckedChangeListener(OnCheckedListener(viewModel::onAttackSortChecked))
            defenseSortCheckbox.setOnCheckedChangeListener(OnCheckedListener(viewModel::onDefenseSortChecked))
            hpSortCheckbox.setOnCheckedChangeListener(OnCheckedListener(viewModel::onHpSortChecked))
        }

        viewModel.onScrollToTopRequested = {
            layoutManager.smoothScrollToPosition(binding.pokemonRv, null, 0)
        }

        return binding.root
    }

    private fun onPokemonClicked(pokemon: Pokemon) {
        val bundle = Bundle().apply { putString("pokemonName", pokemon.name) }
        findNavController().navigate(R.id.action_OverviewFragment_to_detailsFragment, bundle)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        viewModel.onScrollToTopRequested = {}
    }
}

private class EndReachListener(
    private val layoutManager: LinearLayoutManager,
    private val callback: () -> Unit
) : RecyclerView.OnScrollListener() {
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)

        val buffer = 5
        val totalItemsCount = layoutManager.itemCount
        val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
        val isEndReached = lastVisibleItem + buffer > totalItemsCount

        if (isEndReached) callback()
    }
}

private class OnCheckedListener(private val delegate: (Boolean) -> Unit) :
    CompoundButton.OnCheckedChangeListener {
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        delegate(isChecked)
    }
}