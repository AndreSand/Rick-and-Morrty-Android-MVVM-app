package com.android.rickandmortymvvm.data.model

import kotlinx.serialization.Serializable
@Serializable
data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String = "",
    val type: String = "",
    val gender: String = "",
    val origin: Origin = Origin("", ""),
    val location: Location = Location("", ""),
    val image: String,
    val episode: List<String> = emptyList(),
    val url: String = "",
    val created: String = ""
)

@Serializable
data class Origin(
    val name: String,
    val url: String
)

@Serializable
data class Location(
    val name: String,
    val url: String
)

@Serializable
data class CharacterResponse(
    val results: List<Character>
)
