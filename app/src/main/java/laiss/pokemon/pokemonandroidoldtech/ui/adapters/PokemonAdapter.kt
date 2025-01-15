package laiss.pokemon.pokemonandroidoldtech.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import laiss.pokemon.pokemonandroidoldtech.data.models.Pokemon
import laiss.pokemon.pokemonandroidoldtech.databinding.PokemonOverviewCardBinding
import laiss.pokemon.pokemonandroidoldtech.utils.capitalize

class PokemonAdapter(
    private val context: Context,
    private val pokemonList: List<Pokemon>,
    private val onItemClicked: (Pokemon) -> Unit
) :
    RecyclerView.Adapter<PokemonAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: PokemonOverviewCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PokemonOverviewCardBinding
        .inflate(LayoutInflater.from(context), parent, false)
        .let { ViewHolder(it) }

    override fun getItemCount() = pokemonList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pokemon = pokemonList[position]
        with(holder.binding) {
            pokemonName.text = pokemon.name.capitalize()
            Glide.with(context).load(pokemon.imageUrl).into(pokemonImage)
        }
        holder.itemView.setOnClickListener { onItemClicked(pokemon) }
    }
}