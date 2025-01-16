package laiss.pokemon.pokemonandroidoldtech.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import laiss.pokemon.pokemonandroidoldtech.data.models.Pokemon
import laiss.pokemon.pokemonandroidoldtech.databinding.OverviewFragmentBinding
import laiss.pokemon.pokemonandroidoldtech.ui.adapters.PokemonAdapter
import laiss.pokemon.pokemonandroidoldtech.ui.viewModels.OverviewViewModel

class OverviewFragment : Fragment() {
    private lateinit var binding: OverviewFragmentBinding
    private val viewModel: OverviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = OverviewFragmentBinding.inflate(inflater, container, false)

        viewModel.state.observe(viewLifecycleOwner) {
            when (viewModel.state.value) {
                OverviewViewModel.State.Loading -> {
                    binding.errorHeader.isVisible = false
                    binding.errorMessage.isVisible = false
                    binding.pokemonRv.isVisible = false
                    binding.loadingIndicator.isVisible = true
                }

                OverviewViewModel.State.Error -> {
                    binding.errorHeader.isVisible = true
                    binding.errorMessage.isVisible = true
                    binding.pokemonRv.isVisible = false
                    binding.loadingIndicator.isVisible = false
                }

                OverviewViewModel.State.Presenting -> {
                    binding.errorHeader.isVisible = false
                    binding.errorMessage.isVisible = false
                    binding.pokemonRv.isVisible = true
                    binding.loadingIndicator.isVisible = false
                }

                OverviewViewModel.State.LoadingAdditionalPage -> {
                    binding.errorHeader.isVisible = false
                    binding.errorMessage.isVisible = false
                    binding.pokemonRv.isVisible = true
                    binding.loadingIndicator.isVisible = true
                }

                null -> error("LiveData of non-nullable type shouldn't provide null")
            }
        }

        viewModel.lastError.observe(viewLifecycleOwner) {
            binding.errorMessage.text = viewModel.lastError.value
        }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.pokemonRv.layoutManager = layoutManager

        val pokemonAdapter = PokemonAdapter(requireContext(), ::onPokemonClicked)
        binding.pokemonRv.adapter = pokemonAdapter

        viewModel.pokemonList.observe(viewLifecycleOwner) {
            pokemonAdapter.submitList(it)
        }

        binding.pokemonRv.addOnScrollListener(
            EndReachListener(layoutManager, viewModel::startLoadingNextPage)
        )

        return binding.root
    }

    private fun onPokemonClicked(pokemon: Pokemon) {
        Toast.makeText(activity, "Pokemon ${pokemon.name} clicked", Toast.LENGTH_SHORT).show()
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