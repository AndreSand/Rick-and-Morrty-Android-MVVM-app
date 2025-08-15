package com.android.rickandmortymvvm.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.android.rickandmortymvvm.data.model.Character
import com.android.rickandmortymvvm.data.repository.CharacterRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class AppViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: AppViewModel
    private lateinit var mockRepository: CharacterRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty characters list and not loading`() = runTest {
        // Given
        coEvery { mockRepository.getCharacters() } returns emptyList()

        // When
        viewModel = AppViewModel(mockRepository)
        advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.value
        assertTrue(finalState.apps.isEmpty())
        assertFalse(finalState.isLoading)
        assertNull(finalState.error)
    }

    @Test
    fun `fetchCharacters should update state correctly when successful`() = runTest {
        // Given
        val expectedCharacters = listOf(
            Character(id = 1, name = "Rick Sanchez", status = "Human", image = "rick.jpg"),
            Character(id = 2, name = "Morty Smith", status = "Human", image = "morty.jpg")
        )
        coEvery { mockRepository.getCharacters() } returns expectedCharacters

        // When
        viewModel = AppViewModel(mockRepository)

        viewModel.uiState.test {
            // Wait for initial loading and then final state
            advanceUntilIdle()

            // Skip any initial emissions and get the final state
            val finalState = expectMostRecentItem()

            // Then
            assertEquals(expectedCharacters, finalState.apps)
            assertFalse(finalState.isLoading)
            assertNull(finalState.error)
        }
    }

    @Test
    fun `fetchCharacters should set loading state correctly`() = runTest {
        // Given
        coEvery { mockRepository.getCharacters() } returns emptyList()


        // When
        viewModel = AppViewModel(mockRepository)

        viewModel.uiState.test {
            // Initial state
            val initialState = awaitItem()
            assertTrue(initialState.apps.isEmpty())
            assertFalse(initialState.isLoading)
            assertNull(initialState.error)

            // Loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.error)

            advanceUntilIdle()

            // Final state
            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
        }
    }

    @Test
    fun `fetchCharacters should handle error correctly`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { mockRepository.getCharacters() } throws Exception(errorMessage)

        // When
        viewModel = AppViewModel(mockRepository)

        viewModel.uiState.test {
            // Initial state
            awaitItem()

            // Loading state
            awaitItem()

            advanceUntilIdle()

            // Error state
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals(errorMessage, errorState.error)
            assertTrue(errorState.apps.isEmpty())
        }
    }

    @Ignore("Not yet implemented")
    @Test
    fun `fetchCharacters should clear previous error when called again`() = runTest {
        // Given - Repository will fail first, then succeed
        coEvery { mockRepository.getCharacters() } throws Exception("Network error")

        viewModel = AppViewModel(mockRepository)

        viewModel.uiState.test {
            // Wait for initial error state
            awaitItem() // initial
            awaitItem() // loading
            advanceUntilIdle()
            val errorState = awaitItem() // error
            assertEquals("Network error", errorState.error)

            // When - Repository now succeeds
            val characters = listOf(
                Character(
                    id = 1,
                    name = "Rick Sanchez",
                    status = "Human",
                    image = "rick.jpg"
                )
            )
            coEvery { mockRepository.getCharacters() } returns characters

//            viewModel.getCharacters()

            // Loading state for retry
            val retryLoadingState = awaitItem()
            assertTrue(retryLoadingState.isLoading)
            assertNull(retryLoadingState.error) // Error should be cleared

            advanceUntilIdle()

            // Success state
            val successState = awaitItem()
            assertNull(successState.error)
            assertEquals(characters, successState.apps)
            assertFalse(successState.isLoading)
        }
    }

    @Test
    fun `fetchCharacters should call repository fetchCharacters method`() = runTest {
        // Given
        coEvery { mockRepository.getCharacters() } returns emptyList()

        // When
        viewModel = AppViewModel(mockRepository)
        advanceUntilIdle()

        // Then
        coVerify { mockRepository.getCharacters() }
    }

    @Ignore("Not yet implemented")
    @Test
    fun `manual fetchCharacters call should work correctly`() = runTest {
        // Given
        val characters =
            listOf(Character(id = 1, name = "Rick Sanchez", status = "Human", image = "rick.jpg"))
        coEvery { mockRepository.getCharacters() } returns characters

        viewModel = AppViewModel(mockRepository)
        advanceUntilIdle()

        // When - Call fetchCharacters manually
        // viewModel.getCharacters()
        advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.value
        assertEquals(characters, finalState.apps)
        assertFalse(finalState.isLoading)
        assertNull(finalState.error)

        // Should have called fetchCharacters twice (once in init, once manually)
        coVerify(exactly = 2) { mockRepository.getCharacters() }
    }

    @Test
    fun `fetchCharacters should handle multiple characters with different species`() = runTest {
        // Given
        val characters = listOf(
            Character(id = 1, name = "Rick Sanchez", status = "Human", image = "rick.jpg"),
            Character(id = 2, name = "Squanch", status = "Squanch", image = "squanch.jpg"),
            Character(id = 3, name = "Birdperson", status = "Bird-Person", image = "birdperson.jpg")
        )
        coEvery { mockRepository.getCharacters() } returns characters

        // When
        viewModel = AppViewModel(mockRepository)
        advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.value
        assertEquals(3, finalState.apps.size)
        assertEquals("Human", finalState.apps[0].status)
        assertEquals("Squanch", finalState.apps[1].status)
        assertEquals("Bird-Person", finalState.apps[2].status)
    }

    @Test
    fun `fetchCharacters should handle single character response`() = runTest {
        // Given
        val singleCharacter = listOf(
            Character(id = 1, name = "Rick Sanchez", status = "Human", image = "rick.jpg")
        )
        coEvery { mockRepository.getCharacters() } returns singleCharacter

        // When
        viewModel = AppViewModel(mockRepository)
        advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.value
        assertEquals(1, finalState.apps.size)
        assertEquals("Rick Sanchez", finalState.apps[0].name)
        assertFalse(finalState.isLoading)
        assertNull(finalState.error)
    }

    @Test
    fun `state should maintain correct order of characters`() = runTest {
        // Given
        val characters = listOf(
            Character(id = 1, name = "Rick Sanchez", status = "Human", image = "rick.jpg"),
            Character(id = 2, name = "Morty Smith", status = "Human", image = "morty.jpg"),
            Character(id = 3, name = "Summer Smith", status = "Human", image = "summer.jpg")
        )
        coEvery { mockRepository.getCharacters() } returns characters

        // When
        viewModel = AppViewModel(mockRepository)
        advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.value
        assertEquals("Rick Sanchez", finalState.apps[0].name)
        assertEquals("Morty Smith", finalState.apps[1].name)
        assertEquals("Summer Smith", finalState.apps[2].name)
    }
}

// Testable version of AppViewModel that accepts repository as dependency
//class AppViewModel(private val repository: CharacterRepository) : ViewModel() {
//    private val _state = MutableStateFlow(AppState())
//    val state: StateFlow<AppState> = _state.asStateFlow()
//
//    init {
//        fetchCharacters()
//    }
//
//    fun fetchCharacters() {
//        viewModelScope.launch {
//            _state.value = _state.value.copy(isLoading = true, error = null)
//            try {
//                val characters = repository.fetchCharacters()
//                _state.value = _state.value.copy(
//                    apps = characters,
//                    isLoading = false
//                )
//            } catch (e: Exception) {
//                _state.value = _state.value.copy(
//                    isLoading = false,
//                    error = e.message ?: "Unknown error occurred"
//                )
//            }
//        }
//    }
//}
