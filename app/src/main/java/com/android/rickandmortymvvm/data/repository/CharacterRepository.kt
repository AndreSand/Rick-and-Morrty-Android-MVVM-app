package com.android.rickandmortymvvm.data.repository

import com.android.rickandmortymvvm.data.model.Character
import com.android.rickandmortymvvm.data.network.ApiService
import com.android.rickandmortymvvm.data.network.RickandMortyApi

class CharacterRepository(private val api: RickandMortyApi = ApiService.api) {
    suspend fun getCharacters(): List<Character> {
        return try {
            api.getCharacters().results
        }
        catch (e: Exception) {
            throw Exception("Failed to fetch characters ${e.message}")
        }
    }
}