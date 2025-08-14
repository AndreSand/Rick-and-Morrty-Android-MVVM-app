package com.android.rickandmortymvvm.data

import kotlinx.serialization.Serializable
@Serializable
data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val image: String
)

@Serializable
data class CharacterResponse(
    val results: List<Character>
)
