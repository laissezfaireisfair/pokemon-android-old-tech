package laiss.pokemon.pokemonandroidoldtech.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import laiss.pokemon.pokemonandroidoldtech.R
import laiss.pokemon.pokemonandroidoldtech.databinding.FragmentDetailsBinding
import laiss.pokemon.pokemonandroidoldtech.ui.viewModels.DetailsViewModel

private const val POKEMON_NAME_PARAM = "pokemonName"

class DetailsFragment : Fragment() {
    private lateinit var pokemonName: String
    private val viewModel: DetailsViewModel by viewModels {
        DetailsViewModel.Factory(pokemonName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pokemonName = it.getString(POKEMON_NAME_PARAM)
                ?: throw IllegalArgumentException("Pokemon name was not provided when navigating")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDetailsBinding.inflate(inflater, container, false)

        viewModel.state.observe(viewLifecycleOwner) {
            when (viewModel.state.value) {  // TODO: Bind loading and error views visibility
                DetailsViewModel.State.Loading -> {
                    binding.card.isVisible = false
                }

                DetailsViewModel.State.Error -> {
                    binding.card.isVisible = false
                }

                DetailsViewModel.State.Presenting -> {
                    binding.card.isVisible = true
                }

                null -> error("LiveData of non-nullable type shouldn't provide null")
            }
        }

        viewModel.lastError.observe(viewLifecycleOwner) {
            // TODO: Bind to error view
        }

        viewModel.pokemon.observe(viewLifecycleOwner) {
            with (viewModel.pokemon.value) {
                if (this == null) return@observe

                Glide.with(requireContext()).load(imageUrl).into(binding.frontImage)
                binding.name.text = name
                binding.height.text = "${heightDm * 10}"
                binding.weight.text = "${weightHg / 10.0}"
                binding.attack.text = "$attack"
                binding.defence.text = "$defense"
                binding.hp.text = "$hp"
                // TODO: Add types
            }
        }

        return binding.root
    }
}