package laiss.pokemon.pokemonandroidoldtech.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import laiss.pokemon.pokemonandroidoldtech.data.models.Pokemon
import laiss.pokemon.pokemonandroidoldtech.databinding.PokemonOverviewCardBinding
import laiss.pokemon.pokemonandroidoldtech.ui.viewModels.OverviewViewModel

class PokemonAdapter(
    private val context: Context,
    private val entries: List<Pokemon>,
    private val viewModel: OverviewViewModel
) :
    RecyclerView.Adapter<PokemonAdapter.ViewHolder>() {
    inner class ViewHolder(val view: PokemonOverviewCardBinding) :
        RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PokemonOverviewCardBinding
        .inflate(LayoutInflater.from(context), parent, false)
        .let { ViewHolder(it) }

    override fun getItemCount() = entries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.view.pokemonName.text = entry.name
    }
}