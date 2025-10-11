package com.android.rickandmortymvvm.data.mapper

import com.android.rickandmortymvvm.data.mapper.ProtoMapper.toDomain
import com.android.rickandmortymvvm.data.mapper.ProtoMapper.toProto
import com.android.rickandmortymvvm.data.model.Character
import com.android.rickandmortymvvm.data.model.CharacterResponse
import com.android.rickandmortymvvm.data.proto.CharacterProto
import org.junit.Assert.assertEquals
import org.junit.Test

class ProtoMapperTest {

    @Test
    fun `test proto character to domain character mapping`() {
        // Given
        val protoCharacter = CharacterProto.Character.newBuilder()
            .setId(1)
            .setName("Rick Sanchez")
            .setStatus("Alive")
            .setImage("https://rickandmortyapi.com/api/character/avatar/1.jpeg")
            .build()

        // When
        val domainCharacter = protoCharacter.toDomain()

        // Then
        assertEquals(1, domainCharacter.id)
        assertEquals("Rick Sanchez", domainCharacter.name)
        assertEquals("Alive", domainCharacter.status)
        assertEquals("https://rickandmortyapi.com/api/character/avatar/1.jpeg", domainCharacter.image)
    }

    @Test
    fun `test domain character to proto character mapping`() {
        // Given
        val domainCharacter = Character(
            id = 2,
            name = "Morty Smith",
            status = "Alive",
            image = "https://rickandmortyapi.com/api/character/avatar/2.jpeg"
        )

        // When
        val protoCharacter = domainCharacter.toProto()

        // Then
        assertEquals(2, protoCharacter.id)
        assertEquals("Morty Smith", protoCharacter.name)
        assertEquals("Alive", protoCharacter.status)
        assertEquals("https://rickandmortyapi.com/api/character/avatar/2.jpeg", protoCharacter.image)
    }

    @Test
    fun `test proto response to domain response mapping`() {
        // Given
        val protoCharacter1 = CharacterProto.Character.newBuilder()
            .setId(1)
            .setName("Rick Sanchez")
            .setStatus("Alive")
            .setImage("https://rickandmortyapi.com/api/character/avatar/1.jpeg")
            .build()

        val protoCharacter2 = CharacterProto.Character.newBuilder()
            .setId(2)
            .setName("Morty Smith")
            .setStatus("Alive")
            .setImage("https://rickandmortyapi.com/api/character/avatar/2.jpeg")
            .build()

        val protoResponse = CharacterProto.CharacterResponse.newBuilder()
            .addResults(protoCharacter1)
            .addResults(protoCharacter2)
            .build()

        // When
        val domainResponse = protoResponse.toDomain()

        // Then
        assertEquals(2, domainResponse.results.size)
        assertEquals("Rick Sanchez", domainResponse.results[0].name)
        assertEquals("Morty Smith", domainResponse.results[1].name)
    }

    @Test
    fun `test domain response to proto response mapping`() {
        // Given
        val domainResponse = CharacterResponse(
            results = listOf(
                Character(1, "Rick Sanchez", "Alive", "image1.jpg"),
                Character(2, "Morty Smith", "Alive", "image2.jpg")
            )
        )

        // When
        val protoResponse = domainResponse.toProto()

        // Then
        assertEquals(2, protoResponse.resultsCount)
        assertEquals("Rick Sanchez", protoResponse.getResults(0).name)
        assertEquals("Morty Smith", protoResponse.getResults(1).name)
    }

    @Test
    fun `test roundtrip conversion maintains data integrity`() {
        // Given
        val originalCharacter = Character(
            id = 42,
            name = "Test Character",
            status = "Unknown",
            image = "test.jpg"
        )

        // When - Convert to proto and back
        val protoCharacter = originalCharacter.toProto()
        val resultCharacter = protoCharacter.toDomain()

        // Then - Should be identical
        assertEquals(originalCharacter.id, resultCharacter.id)
        assertEquals(originalCharacter.name, resultCharacter.name)
        assertEquals(originalCharacter.status, resultCharacter.status)
        assertEquals(originalCharacter.image, resultCharacter.image)
    }

    @Test
    fun `test empty character list mapping`() {
        // Given
        val emptyProtoResponse = CharacterProto.CharacterResponse.newBuilder()
            .build()

        // When
        val domainResponse = emptyProtoResponse.toDomain()

        // Then
        assertEquals(0, domainResponse.results.size)
    }
}
