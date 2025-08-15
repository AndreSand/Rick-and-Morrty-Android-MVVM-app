package com.android.rickandmortymvvm.data.paging

import androidx.paging.PagingSource
import com.android.rickandmortymvvm.data.model.Character
import com.android.rickandmortymvvm.data.model.CharacterResponse
import com.android.rickandmortymvvm.data.model.Info
import com.android.rickandmortymvvm.data.network.RickandMortyApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class CharacterPagingSourceTest {

    private val mockApi = mockk<RickandMortyApi>()
    private val pagingSource = CharacterPagingSource(mockApi)

    @Test
    fun `load returns Page when api call is successful`() = runTest {
        // Given
        val characters = listOf(
            Character(1, "Rick Sanchez", "Alive", "https://rickandmortyapi.com/api/character/avatar/1.jpeg"),
            Character(2, "Morty Smith", "Alive", "https://rickandmortyapi.com/api/character/avatar/2.jpeg")
        )
        val info = Info(count = 826, pages = 42, next = "page=2", prev = null)
        val response = CharacterResponse(characters, info)

        coEvery { mockApi.getCharacters(1) } returns response

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(characters, page.data)
        assertEquals(null, page.prevKey)
        assertEquals(2, page.nextKey)
    }

    @Test
    fun `load returns Error when api call fails`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        coEvery { mockApi.getCharacters(any()) } throws exception

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Error)
        val error = result as PagingSource.LoadResult.Error
        assertEquals(exception, error.throwable)
    }

    @Test
    fun `load returns correct keys for middle page`() = runTest {
        // Given
        val characters = listOf(
            Character(1, "Rick Sanchez", "Alive", "https://rickandmortyapi.com/api/character/avatar/1.jpeg")
        )
        val info = Info(count = 826, pages = 42, next = "page=3", prev = "page=1")
        val response = CharacterResponse(characters, info)

        coEvery { mockApi.getCharacters(2) } returns response

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 2,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(1, page.prevKey)
        assertEquals(3, page.nextKey)
    }

    @Test
    fun `load returns null nextKey for last page`() = runTest {
        // Given
        val characters = listOf(
            Character(1, "Rick Sanchez", "Alive", "https://rickandmortyapi.com/api/character/avatar/1.jpeg")
        )
        val info = Info(count = 826, pages = 42, next = null, prev = "page=41")
        val response = CharacterResponse(characters, info)

        coEvery { mockApi.getCharacters(42) } returns response

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 42,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(41, page.prevKey)
        assertEquals(null, page.nextKey)
    }
}
