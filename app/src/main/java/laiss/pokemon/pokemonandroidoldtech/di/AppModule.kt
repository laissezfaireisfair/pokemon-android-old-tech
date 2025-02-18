package laiss.pokemon.pokemonandroidoldtech.di

import laiss.pokemon.pokemonandroidoldtech.data.IPokemonRepository
import laiss.pokemon.pokemonandroidoldtech.data.PokemonRepository
import laiss.pokemon.pokemonandroidoldtech.data.dataSources.LocalStorageDataSource
import laiss.pokemon.pokemonandroidoldtech.data.dataSources.PokeApiDataSource
import okhttp3.OkHttpClient
import org.koin.dsl.module

private val numberOfCores: Int by lazy { Runtime.getRuntime().availableProcessors() }

val appModule = module {
    single { OkHttpClient() }
    single { LocalStorageDataSource(get()) }
    single { PokeApiDataSource(get()) }
    single<IPokemonRepository> { PokemonRepository(get(), get(), 30) }
}