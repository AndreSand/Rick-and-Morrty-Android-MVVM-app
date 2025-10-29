package com.android.rickandmortymvvm.di

import com.android.rickandmortymvvm.data.network.RickandMortyApi
import com.android.rickandmortymvvm.data.repository.CharacterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
        }
    }

    @Singleton
    @Provides
    fun provideRickandMortyApi(json: Json): RickandMortyApi {
        val BASE_URL = "https://rickandmortyapi.com/api/"
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(RickandMortyApi::class.java)
    }

    @Singleton
    @Provides
    fun provideCharacterRepository(api: RickandMortyApi): CharacterRepository {
        return CharacterRepository(api)
    }
}