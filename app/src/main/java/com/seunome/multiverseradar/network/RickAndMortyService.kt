package com.seunome.multiverseradar.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class CharacterResponse(
    val results: List<CharacterDto>
)

data class CharacterDto(
    val id: Int,
    val name: String,
    val species: String,
    val status: String,
    val image: String
)

interface RickAndMortyService {
    @GET("character")
    suspend fun getCharacters(): CharacterResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://rickandmortyapi.com/api/"

    val api: RickAndMortyService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RickAndMortyService::class.java)
    }
}
