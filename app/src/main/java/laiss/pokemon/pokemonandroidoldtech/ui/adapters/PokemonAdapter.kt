package laiss.pokemon.pokemonandroidoldtech.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import laiss.pokemon.pokemonandroidoldtech.data.models.Pokemon
import laiss.pokemon.pokemonandroidoldtech.databinding.PokemonOverviewCardBinding
import laiss.pokemon.pokemonandroidoldtech.utils.capitalize

class PokemonAdapter(
    private val context: Context,
    private val onItemClicked: (Pokemon) -> Unit
) : RecyclerView.Adapter<PokemonAdapter.ViewHolder>() {
    private val diffUtil = object : DiffUtil.ItemCallback<Pokemon>() {
        override fun areItemsTheSame(oldItem: Pokemon, newItem: Pokemon) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Pokemon, newItem: Pokemon) =
            oldItem == newItem
    }

    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    inner class ViewHolder(val binding: PokemonOverviewCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PokemonOverviewCardBinding
        .inflate(LayoutInflater.from(context), parent, false)
        .let { ViewHolder(it) }

    override fun getItemCount() = asyncListDiffer.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pokemon = asyncListDiffer.currentList[position]
        with(holder.binding) {
            pokemonName.text = pokemon.name.capitalize()
            Glide.with(context).load(pokemon.imageUrl).into(pokemonImage)
        }
        holder.itemView.setOnClickListener { onItemClicked(pokemon) }
    }

    fun submitList(page: List<Pokemon>) = asyncListDiffer.submitList(page)
}