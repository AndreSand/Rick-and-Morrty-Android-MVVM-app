package com.android.rickandmortymvvm.navigation

import com.android.rickandmortymvvm.navigation.CharacterDetail
import com.android.rickandmortymvvm.navigation.CharacterList
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import org.junit.Assert.*

/**
 * Test Navigation 3 routes serialization
 */
class NavigationTest {

    @Test
    fun `CharacterList route serialization works correctly`() {
        val route = CharacterList
        val json = Json.encodeToString(route)
        val deserializedRoute = Json.decodeFromString<CharacterList>(json)
        
        assertEquals(route, deserializedRoute)
    }

    @Test
    fun `CharacterDetail route serialization works correctly`() {
        val characterId = 123
        val route = CharacterDetail(characterId)
        val json = Json.encodeToString(route)
        val deserializedRoute = Json.decodeFromString<CharacterDetail>(json)
        
        assertEquals(route, deserializedRoute)
        assertEquals(characterId, deserializedRoute.characterId)
    }

    @Test
    fun `CharacterDetail route contains correct character ID`() {
        val expectedId = 42
        val route = CharacterDetail(expectedId)
        
        assertEquals(expectedId, route.characterId)
    }
}
