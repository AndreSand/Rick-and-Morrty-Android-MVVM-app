package com.android.rickandmortymvvm.data.model

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
    val results: List<Character>,
    val info: Info
)

@Serializable
data class Info(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)
