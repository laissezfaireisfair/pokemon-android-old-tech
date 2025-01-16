package laiss.pokemon.pokemonandroidoldtech.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import laiss.pokemon.pokemonandroidoldtech.R

private const val POKEMON_NAME_PARAM = "pokemonName"

class DetailsFragment : Fragment() {
    private var pokemonName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { pokemonName = it.getString(POKEMON_NAME_PARAM) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }
}