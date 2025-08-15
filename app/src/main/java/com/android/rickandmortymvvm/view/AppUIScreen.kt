package com.android.rickandmortymvvm.view

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.android.rickandmortymvvm.viewmodel.AppViewModel

@Composable
fun MainScreen(viewModel: AppViewModel, modifier: Modifier) {

    val state by viewModel.uiState.collectAsState()

    when {
        state.isLoading -> CircularProgressIndicator()
        state.error != null -> Text("Error: ${state.error}")
        state.apps.isEmpty() -> Text("No apps found")

        else ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.apps) { app ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 36.dp)
                        ) {
                            Text(
                                text = app.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "ID: ${app.id}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            AsyncImage(
                                model = app.image,
                                contentDescription = app.name,
                                modifier = Modifier.size(128.dp)
                            )
                        }
                    }
                }
            }
    }
}