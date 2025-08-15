package com.android.rickandmortymvvm.navigation

//import androidx.navigation3.keys.NavKey
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Navigation routes for the Rick and Morty app using Navigation 3
 */

@Serializable
data object CharacterList : NavKey

@Serializable
data class CharacterDetail(val characterId: Int) : NavKey
