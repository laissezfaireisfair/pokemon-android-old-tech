package laiss.pokemon.pokemonandroidoldtech.di

import android.os.Looper
import androidx.core.os.HandlerCompat
import laiss.pokemon.pokemonandroidoldtech.data.IPokemonRepository
import laiss.pokemon.pokemonandroidoldtech.data.PokemonRepository
import laiss.pokemon.pokemonandroidoldtech.data.dataSources.LocalStorageDataSource
import laiss.pokemon.pokemonandroidoldtech.data.dataSources.PokeApiDataSource
import okhttp3.OkHttpClient
import org.koin.dsl.module
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

private val numberOfCores: Int by lazy { Runtime.getRuntime().availableProcessors() }

val appModule = module {
    single {
        ThreadPoolExecutor(
            numberOfCores,
            numberOfCores,
            1,
            TimeUnit.SECONDS,
            LinkedBlockingQueue()
        )
    }
    single { HandlerCompat.createAsync(Looper.getMainLooper()) }
    single { OkHttpClient() }
    single { LocalStorageDataSource(get()) }
    single { PokeApiDataSource(get()) }
    single<IPokemonRepository> { PokemonRepository(get(), get(), get(), get(), 30) }
}