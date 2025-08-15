package com.android.rickandmortymvvm.data.model

import org.junit.Assert
import org.junit.Test

class CharacterTest {

    @Test
    fun `character data class should hold correct values`() {
        // Given
        val id = 1
        val name = "Rick Sanchez"
        val status = "Human"
        val image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"

        // When
        val character = Character(
            id = id,
            name = name,
            status = status,
            image = image
        )

        // Then
        Assert.assertEquals(id, character.id)
        Assert.assertEquals(name, character.name)
        Assert.assertEquals(status, character.status)
        Assert.assertEquals(image, character.image)
    }

    @Test
    fun `character should support equality comparison`() {
        // Given
        val character1 = Character(
            id = 1,
            name = "Rick Sanchez",
            status = "Human",
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
        )
        val character2 = Character(
            id = 1,
            name = "Rick Sanchez",
            status = "Human",
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
        )

        // Then
        Assert.assertEquals(character1, character2)
    }

    @Test
    fun `character response should hold list of characters`() {
        // Given
        val characters = listOf(
            Character(
                id = 1,
                name = "Rick Sanchez",
                status = "Human",
                image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
            ),
            Character(
                id = 2,
                name = "Morty Smith",
                status = "Human",
                image = "https://rickandmortyapi.com/api/character/avatar/2.jpeg"
            )
        )

        // When
        val response = CharacterResponse(results = characters)

        // Then
        Assert.assertEquals(characters, response.results)
        Assert.assertEquals(2, response.results.size)
    }

    @Test
    fun `character response should handle empty list`() {
        // Given
        val emptyList = emptyList<Character>()

        // When
        val response = CharacterResponse(results = emptyList)

        // Then
        Assert.assertEquals(emptyList, response.results)
        Assert.assertEquals(0, response.results.size)
    }
}