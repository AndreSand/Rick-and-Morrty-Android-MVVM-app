package com.android.rickandmortymvvm.data.network

import com.android.rickandmortymvvm.data.model.CharacterResponse
import retrofit2.http.GET

//class ApiService {
interface RickandMortyApi {
    @GET("character")
    suspend fun getCharacters(): CharacterResponse
}
