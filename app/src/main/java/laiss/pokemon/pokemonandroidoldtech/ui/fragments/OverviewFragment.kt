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

        viewModel.error.observe(viewLifecycleOwner) {
            binding.errorHeader.isVisible = viewModel.error.value != null
            binding.errorMessage.isVisible = viewModel.error.value != null
            binding.pokemonRv.isVisible = viewModel.error.value == null
            binding.errorMessage.text = viewModel.error.value
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.errorHeader.isVisible = viewModel.isLoading.value!!.not()
            binding.errorMessage.isVisible = viewModel.isLoading.value!!.not()
            binding.pokemonRv.isVisible = viewModel.isLoading.value!!.not()
            binding.loadingIndicator.isVisible = viewModel.isLoading.value!!
        }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.pokemonRv.layoutManager = layoutManager

        viewModel.pokemonList.observe(viewLifecycleOwner) {
            binding.pokemonRv.adapter = PokemonAdapter(requireContext(), it, ::onPokemonClicked)
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
    var isAlreadyReported = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val buffer = 5
        val visibleItemsCount = recyclerView.childCount
        val totalItemsCount = layoutManager.itemCount
        val lastVisibleItem = layoutManager.findFirstVisibleItemPosition() + visibleItemsCount - 1
        val isEndReached = lastVisibleItem + buffer > totalItemsCount

        if (isEndReached) {
            if (!isAlreadyReported) {
                isAlreadyReported = true
                callback()
            }
        } else {
            isAlreadyReported = false
        }
    }
}