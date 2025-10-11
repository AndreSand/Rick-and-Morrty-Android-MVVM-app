package com.android.rickandmortymvvm.data.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.protobuf.ProtoConverterFactory

object ApiClient {
    const val BASE_URL = "https://rickandmortyapi.com/api/"

    val json = Json {
        ignoreUnknownKeys = true
    }

    // JSON-based API (existing)
    val api: RickandMortyApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(RickandMortyApi::class.java)
    }
    
    // Protocol Buffers-based API (new)
    val protoApi: RickandMortyProtoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ProtoConverterFactory.create())
            .build()
            .create(RickandMortyProtoApi::class.java)
    }
}
