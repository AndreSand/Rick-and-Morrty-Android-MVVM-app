package com.android.rickandmortymvvm.viewmodel

import com.android.rickandmortymvvm.data.model.Character
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test


class AppStateTest {

    @Test
    fun `default AppState should have empty list, not loading, and no error`() {
        // When
        val state = AppUiState()

        // Then
        assertTrue(state.apps.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `AppState should hold characters correctly`() {
        // Given
        val characters = listOf(
            Character(id = 1, name = "Rick Sanchez", status = "Human", image = "rick.jpg"),
            Character(id = 2, name = "Morty Smith", status = "Human", image = "morty.jpg")
        )

        // When
        val state = AppUiState(apps = characters)

        // Then
        assertEquals(characters, state.apps)
        assertEquals(2, state.apps.size)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `AppState should handle loading state`() {
        // When
        val state = AppUiState(isLoading = true)

        // Then
        assertTrue(state.isLoading)
        assertTrue(state.apps.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `AppState should handle error state`() {
        // Given
        val errorMessage = "Network error"

        // When
        val state = AppUiState(error = errorMessage)

        // Then
        assertEquals(errorMessage, state.error)
        assertTrue(state.apps.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun `AppState should support all combinations of properties`() {
        // Given
        val characters = listOf(
            Character(id = 1, name = "Rick Sanchez", status = "Human", image = "rick.jpg")
        )
        val errorMessage = "Some error"

        // When
        val state = AppUiState(
            apps = characters,
            isLoading = true,
            error = errorMessage
        )

        // Then
        assertEquals(characters, state.apps)
        assertTrue(state.isLoading)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `AppState should support copy function for immutable updates`() {
        // Given
        val initialState = AppUiState()
        val characters = listOf(
            Character(id = 1, name = "Rick Sanchez", status = "Human", image = "rick.jpg")
        )

        // When
        val loadingState = initialState.copy(isLoading = true)
        val successState = loadingState.copy(apps = characters, isLoading = false)
        val errorState = successState.copy(error = "Error occurred")

        // Then
        assertTrue(loadingState.isLoading)
        assertTrue(loadingState.apps.isEmpty())

        assertEquals(characters, successState.apps)
        assertFalse(successState.isLoading)
        assertNull(successState.error)

        assertEquals(characters, errorState.apps)
        assertFalse(errorState.isLoading)
        assertEquals("Error occurred", errorState.error)
    }

    @Test
    fun `AppState copy should preserve unchanged properties`() {
        // Given
        val characters = listOf(
            Character(id = 1, name = "Rick Sanchez", status = "Human", image = "rick.jpg")
        )
        val originalState = AppUiState(apps = characters, isLoading = false, error = null)

        // When
        val newState = originalState.copy(isLoading = true)

        // Then
        assertEquals(characters, newState.apps) // Should be preserved
        assertTrue(newState.isLoading) // Should be changed
        assertNull(newState.error) // Should be preserved
    }
}
