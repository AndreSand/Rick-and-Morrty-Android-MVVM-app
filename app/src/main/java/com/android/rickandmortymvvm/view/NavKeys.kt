package com.android.rickandmortymvvm.view

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object HomeScreen : NavKey

@Serializable
data class DetailScreen(val characterId: Int) : NavKey
