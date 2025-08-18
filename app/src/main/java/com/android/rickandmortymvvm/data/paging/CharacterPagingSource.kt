package com.android.rickandmortymvvm.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.rickandmortymvvm.data.model.Character
import com.android.rickandmortymvvm.data.network.RickandMortyApi
import kotlinx.coroutines.delay

class CharacterPagingSource(
    private val api: RickandMortyApi
) : PagingSource<Int, Character>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        return try {
            val page = params.key ?: 1
            
            // Add a small delay to make loading states more visible for demonstration
            delay(500)
            
            val response = api.getCharacters(page)
            
            LoadResult.Page(
                data = response.results,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.info.next == null) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
