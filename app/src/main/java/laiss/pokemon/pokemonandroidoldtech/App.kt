package laiss.pokemon.pokemonandroidoldtech

import android.app.Application
import laiss.pokemon.pokemonandroidoldtech.di.appModule
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin { modules(appModule) }
    }
}