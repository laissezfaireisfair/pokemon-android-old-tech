package laiss.pokemon.pokemonandroidoldtech.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import laiss.pokemon.pokemonandroidoldtech.databinding.OverviewCardBinding
import laiss.pokemon.pokemonandroidoldtech.ui.viewModels.OverviewScreenEntry
import laiss.pokemon.pokemonandroidoldtech.ui.viewModels.OverviewScreenViewModel

class OverviewAdapter(
    private val context: Context,
    private val entries: List<OverviewScreenEntry>,
    private val viewModel: OverviewScreenViewModel
) :
    RecyclerView.Adapter<OverviewAdapter.ViewHolder>() {
    inner class ViewHolder(val view: OverviewCardBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OverviewCardBinding
        .inflate(LayoutInflater.from(context), parent, false)
        .let { ViewHolder(it) }

    override fun getItemCount() = entries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.view.pokemonName.text = entry.name
    }
}