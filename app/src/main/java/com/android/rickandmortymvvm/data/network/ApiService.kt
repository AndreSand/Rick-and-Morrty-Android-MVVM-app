package com.android.rickandmortymvvm.data.network

import com.android.rickandmortymvvm.data.model.CharacterResponse
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET

interface RickandMortyApi {
    @GET("character")
    suspend fun getCharacters(): CharacterResponse
}

object ApiService {
    const val BASE_URL = "https://rickandmortyapi.com/api/"

    val json = Json {
        ignoreUnknownKeys = true
    }

    val api: RickandMortyApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(RickandMortyApi::class.java)
    }
}