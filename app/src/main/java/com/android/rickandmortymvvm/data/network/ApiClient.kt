package com.android.rickandmortymvvm.data.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object ApiClient {
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