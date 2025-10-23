package com.android.rickandmortymvvm.mvi

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.android.rickandmortymvvm.data.model.Character

/**
 * MVI Screen for displaying characters
 * 
 * Responsibilities:
 * 1. Observe state from ViewModel
 * 2. Render UI based on current state
 * 3. Send intents to ViewModel on user actions
 * 4. Handle side effects (toasts, navigation, etc.)
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CharacterScreen(
    viewModel: CharacterViewModel,
    modifier: Modifier = Modifier
) {
    // Observe state
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Handle side effects
    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is CharacterSideEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is CharacterSideEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
                is CharacterSideEffect.NavigateToDetails -> {
                    // Handle navigation
                    Toast.makeText(
                        context,
                        "Navigate to character ${effect.characterId}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Render UI based on state
    when (val currentState = state) {
        is CharacterState.Idle -> {
            // Initial state - could show placeholder
            IdleContent()
        }

        is CharacterState.Loading -> {
            // Show loading indicator
            LoadingContent()
        }

        is CharacterState.Success -> {
            // Show character list with pull-to-refresh
            val pullRefreshState = rememberPullRefreshState(
                refreshing = currentState.isRefreshing,
                onRefresh = { viewModel.handleIntent(CharacterIntent.RefreshCharacters) }
            )

            Box(
                modifier = modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
            ) {
                CharacterListContent(
                    characters = currentState.characters,
                    onCharacterClick = { characterId ->
                        // Send intent for character selection
                        // Could trigger navigation side effect
                    }
                )

                PullRefreshIndicator(
                    refreshing = currentState.isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }

        is CharacterState.Error -> {
            // Show error with retry option
            ErrorContent(
                message = currentState.message,
                previousCharacters = currentState.previousCharacters,
                onRetry = { viewModel.handleIntent(CharacterIntent.RetryLoadCharacters) }
            )
        }
    }
}

@Composable
private fun IdleContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Ready to load characters",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Loading characters...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun CharacterListContent(
    characters: List<Character>,
    onCharacterClick: (Int) -> Unit
) {
    if (characters.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No characters found",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = characters,
                key = { it.id }
            ) { character ->
                CharacterCard(
                    character = character,
                    onClick = { onCharacterClick(character.id) }
                )
            }
        }
    }
}

@Composable
private fun CharacterCard(
    character: Character,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 36.dp, top = 8.dp, bottom = 8.dp)
        ) {
            Text(
                text = character.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ID: ${character.id} • Status: ${character.status}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            AsyncImage(
                model = character.image,
                contentDescription = character.name,
                modifier = Modifier.size(128.dp)
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    previousCharacters: List<Character>,
    onRetry: () -> Unit
) {
    if (previousCharacters.isEmpty()) {
        // Full error state with no previous data
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "⚠️",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = "Oops! Something went wrong",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    } else {
        // Show previous data with error banner
        Column(modifier = Modifier.fillMaxSize()) {
            // Error banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Error: $message",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onRetry) {
                        Text("Retry")
                    }
                }
            }

            // Show previous characters
            CharacterListContent(
                characters = previousCharacters,
                onCharacterClick = {}
            )
        }
    }
}
