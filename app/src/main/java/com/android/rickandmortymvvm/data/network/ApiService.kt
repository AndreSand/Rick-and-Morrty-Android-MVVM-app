package com.android.rickandmortymvvm.data.network

import com.android.rickandmortymvvm.data.model.CharacterResponse
import retrofit2.http.GET
import retrofit2.http.Query

//class ApiService {
interface RickandMortyApi {
    @GET("character")
    suspend fun getCharacters(): CharacterResponse
    
    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int): CharacterResponse
}
