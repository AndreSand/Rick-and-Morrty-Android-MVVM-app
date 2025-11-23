package com.android.rickandmortymvvm.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.android.rickandmortymvvm.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailsScreen(
    appViewModel: AppViewModel,
    characterId: Int,
    onBack: () -> Unit,
    modifier: Modifier
) {
    val state by appViewModel.uiState.collectAsState()
    val character = state.apps.find { it.id == characterId }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Details") })
        },
//        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Button(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            if (character != null) {
                Text(text = character.name)

                AsyncImage(
                    model = character.image,
                    contentDescription = character.name,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(text = "Character not found")
            }
        }
    }
}
