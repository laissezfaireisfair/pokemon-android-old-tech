package laiss.pokemon.pokemonandroidoldtech.data.dataSources

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String?,
    val height: Int,
    val weight: Int,
    val types: String,
    val attack: Int,
    val defense: Int,
    val hp: Int
)

class LocalStorageDataSource(applicationContext: Context) {
    private val localDatabase =
        Room.databaseBuilder(applicationContext, LocalDatabase::class.java, "local-db").build()

    fun getPokemonList() = localDatabase.pokemonDao().loadAllPokemon()

    fun storePokemon(pokemon: PokemonEntity) =
        localDatabase.pokemonDao().insertPokemon(pokemon)
}

@Dao
internal interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPokemon(pokemon: PokemonEntity)

    @Query("SELECT * from pokemon")
    fun loadAllPokemon(): List<PokemonEntity>
}

@Database(entities = [PokemonEntity::class], version = 1)
internal abstract class LocalDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
}