package com.android.rickandmortymvvm.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.android.rickandmortymvvm.data.model.Character
import com.android.rickandmortymvvm.data.network.ApiService
import com.android.rickandmortymvvm.data.network.RickandMortyApi
import com.android.rickandmortymvvm.data.paging.CharacterPagingSource
import kotlinx.coroutines.flow.Flow

class CharacterRepository(private val api: RickandMortyApi = ApiService.api) {

    suspend fun getCharacters(): List<Character> {
        return api.getCharacters().results
    }
    
    fun getCharactersPaginated(): Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20, // Rick and Morty API returns 20 items per page by default
                prefetchDistance = 2,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                CharacterPagingSource(api)
            }
        ).flow
    }
}