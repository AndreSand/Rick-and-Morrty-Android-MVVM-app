package com.android.rickandmortymvvm.repository

import com.android.rickandmortymvvm.data.Character
import com.android.rickandmortymvvm.network.ApiService
import com.android.rickandmortymvvm.network.RickandMortyApi

class CharacterRepository(private val api: RickandMortyApi = ApiService.api) {

    suspend fun getCharacters(): List<Character> {
        return api.getCharacters().results
    }
}