package laiss.pokemon.pokemonandroidoldtech.ui.viewModels

import androidx.lifecycle.ViewModel
import laiss.pokemon.pokemonandroidoldtech.data.IPokemonRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DetailsScreenViewModel : ViewModel(), KoinComponent {
    private val pokemonRepository: IPokemonRepository by inject()
}