package laiss.pokemon.pokemonandroidoldtech.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import laiss.pokemon.pokemonandroidoldtech.databinding.FragmentDetailsBinding
import laiss.pokemon.pokemonandroidoldtech.ui.viewModels.DetailsViewModel
import laiss.pokemon.pokemonandroidoldtech.utils.capitalize

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

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDetailsBinding.inflate(inflater, container, false)

        scope.launch {
            viewModel.state.collect {
                when (it) {
                    DetailsViewModel.State.Loading -> {
                        with(binding) {
                            card.visibility = View.INVISIBLE
                            errorLayout.visibility = View.GONE
                            loadingIndicator.visibility = View.VISIBLE
                        }
                    }

                    DetailsViewModel.State.Error -> {
                        with(binding) {
                            card.visibility = View.INVISIBLE
                            errorLayout.visibility = View.VISIBLE
                            loadingIndicator.visibility = View.GONE
                        }
                    }

                    DetailsViewModel.State.Presenting -> {
                        with(binding) {
                            card.visibility = View.VISIBLE
                            errorLayout.visibility = View.GONE
                            loadingIndicator.visibility = View.GONE
                        }
                    }
                }
            }
        }

        scope.launch { viewModel.lastError.collect { binding.errorMessage.text = it } }

        scope.launch {
            viewModel.pokemon.collect {
                with(binding) {
                    if (it == null) return@with

                    Glide.with(requireContext()).load(it.imageUrl).into(frontImage)
                    name.text = it.name.capitalize()
                    height.text = "${it.heightDm * 10}"
                    weight.text = "${it.weightHg / 10.0}"
                    attack.text = "${it.attack}"
                    defence.text = "${it.defense}"
                    hp.text = "${it.hp}"

                    typesList.removeAllViews()
                    it.types.forEach {
                        typesList.addView(TextView(requireContext()).apply { text = it.typeString })
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}