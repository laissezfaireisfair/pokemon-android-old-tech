package laiss.pokemon.pokemonandroidoldtech.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import laiss.pokemon.pokemonandroidoldtech.databinding.OverviewFragmentBinding
import laiss.pokemon.pokemonandroidoldtech.ui.adapters.OverviewAdapter
import laiss.pokemon.pokemonandroidoldtech.ui.viewModels.OverviewScreenViewModel

class OverviewFragment : Fragment() {
    private lateinit var binding: OverviewFragmentBinding
    private val viewModel: OverviewScreenViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = OverviewFragmentBinding.inflate(inflater, container, false)

        binding.pokemonRv.layoutManager = LinearLayoutManager(requireContext())

        viewModel.entries.observe(viewLifecycleOwner) {
            binding.pokemonRv.adapter = OverviewAdapter(requireContext(), it, viewModel)
        }

        return binding.root
    }
}