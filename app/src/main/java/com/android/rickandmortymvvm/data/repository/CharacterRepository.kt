package com.android.rickandmortymvvm.data.repository

import com.android.rickandmortymvvm.data.model.Character
import com.android.rickandmortymvvm.data.network.RickandMortyApi
import javax.inject.Inject

class CharacterRepository @Inject constructor(private val api: RickandMortyApi) {
    suspend fun getCharacters(): List<Character> {
        return try {
            api.getCharacters().results
        }
        catch (e: Exception) {
            throw Exception("Failed to fetch characters ${e.message}")
        }
    }
}