package laiss.pokemon.pokemonandroidoldtech.data.dataSources

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class PokeApiDataSource(private val client: OkHttpClient) {
    private val baseUrl = "https://pokeapi.co/api/v2"

    fun getPokemonHeadersList(offset: Int, count: Int) =
        preformGetRequest<PokemonHeadersListDto>("$baseUrl/pokemon/?limit=$count&offset=$offset")

    fun getPokemon(name: String) = preformGetRequest<PokemonDto>("$baseUrl/pokemon/$name/")

    private inline fun <reified T> preformGetRequest(url: String) {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful.not()) throw IOException("Request failed: $response")
            response.body?.string()
        }?.let {
            try {
                val json = Json { ignoreUnknownKeys = true }
                json.decodeFromString<T>(it)
            } catch (exception: SerializationException) {
                throw IOException("Bad JSON received $exception")
            } catch (exception: IllegalArgumentException) {
                throw IOException("Bad type of received body: $exception}")
            } catch (exception: Exception) {
                throw IOException("Failed to deserialize: $exception")
            }
        } ?: throw IOException("Empty body received")
    }
}