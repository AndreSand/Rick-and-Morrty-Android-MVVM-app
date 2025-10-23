# Android MVI Architecture - Rick and Morty Character App

## Project Setup
1. Create new Android Studio app project
2. Run app on device
3. Import required libraries

### Dependencies (build.gradle.kts)

```kotlin
dependencies {
    // Compose & Material 3
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    
    // Retrofit for networking
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    
    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:3.0.0")
    
    // Coil for image loading
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")
    
    // Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")
    
    // Material pull-to-refresh for MVI
    implementation("androidx.compose.material:material:1.7.8")
    
    // Testing (Optional)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("app.cash.turbine:turbine:1.1.0")
}
```

---

## 1. Data Model

### Create Data Package Structure
```
app/src/main/java/com/android/rickandmortymvvm/
├── data/
│   ├── model/
│   │   └── Character.kt
│   ├── network/
│   │   ├── ApiClient.kt
│   │   └── ApiService.kt
│   └── repository/
│       └── CharacterRepository.kt
```

### app/data/model/Character.kt
```kotlin
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
    val results: List<Character>
)
```

**Key Points:**
- Use `@Serializable` annotation for kotlinx.serialization
- Create separate response wrapper class
- Keep models immutable (val)

---

## 2. Network Layer

### app/data/network/ApiService.kt
```kotlin
package com.android.rickandmortymvvm.data.network

import com.android.rickandmortymvvm.data.model.CharacterResponse
import retrofit2.http.GET

interface RickAndMortyApi {
    @GET("character")
    suspend fun getCharacters(): CharacterResponse
}
```

### app/data/network/ApiClient.kt
```kotlin
package com.android.rickandmortymvvm.data.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object ApiClient {
    const val BASE_URL = "https://rickandmortyapi.com/api/"

    val json = Json {
        ignoreUnknownKeys = true
    }

    val api: RickAndMortyApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(RickAndMortyApi::class.java)
    }
}
```

**Key Points:**
- Use `suspend` functions for coroutines
- Configure Json to ignore unknown keys
- Use lazy initialization for API client

---

## 3. Repository Layer

### app/data/repository/CharacterRepository.kt
```kotlin
package com.android.rickandmortymvvm.data.repository

import com.android.rickandmortymvvm.data.model.Character
import com.android.rickandmortymvvm.data.network.ApiClient
import com.android.rickandmortymvvm.data.network.RickAndMortyApi

class CharacterRepository(
    private val api: RickAndMortyApi = ApiClient.api
) {
    suspend fun getCharacters(): List<Character> {
        return try {
            api.getCharacters().results
        } catch (e: Exception) {
            throw Exception("Failed to fetch characters: ${e.message}")
        }
    }
}
```

**Key Points:**
- Single responsibility: data fetching only
- Proper error handling
- Testable with dependency injection

---

## 4. MVI Architecture

### Create MVI Package Structure
```
app/src/main/java/com/android/rickandmortymvvm/
└── mvi/
    ├── CharacterIntent.kt
    ├── CharacterState.kt
    ├── CharacterSideEffect.kt
    ├── CharacterViewModel.kt
    └── CharacterScreen.kt
```

---

### 4.1 Intent - User Actions

### app/mvi/CharacterIntent.kt
```kotlin
package com.android.rickandmortymvvm.mvi

/**
 * Represents all possible user intentions/actions
 */
sealed interface CharacterIntent {
    /**
     * User wants to load characters for the first time
     */
    data object LoadCharacters : CharacterIntent
    
    /**
     * User wants to refresh the character list
     */
    data object RefreshCharacters : CharacterIntent
    
    /**
     * User wants to retry after an error
     */
    data object RetryLoadCharacters : CharacterIntent
}
```

**Key Points:**
- Use `sealed interface` for exhaustive when expressions
- Each intent represents a distinct user action
- Use `data object` for parameter-less intents

---

### 4.2 State - UI States

### app/mvi/CharacterState.kt
```kotlin
package com.android.rickandmortymvvm.mvi

import com.android.rickandmortymvvm.data.model.Character

/**
 * Represents all possible UI states
 */
sealed interface CharacterState {
    /**
     * Initial idle state before any data loading
     */
    data object Idle : CharacterState
    
    /**
     * Loading state - showing progress indicator
     */
    data object Loading : CharacterState
    
    /**
     * Success state with character data
     */
    data class Success(
        val characters: List<Character>,
        val isRefreshing: Boolean = false
    ) : CharacterState
    
    /**
     * Error state with error message
     */
    data class Error(
        val message: String,
        val previousCharacters: List<Character> = emptyList()
    ) : CharacterState
}
```

**Key Points:**
- States are mutually exclusive (only one at a time)
- States are immutable
- Include all necessary data in each state
- Preserve previous data when appropriate

---

### 4.3 Side Effect - One-Time Events

### app/mvi/CharacterSideEffect.kt
```kotlin
package com.android.rickandmortymvvm.mvi

/**
 * Represents one-time side effects
 * Examples: navigation, toasts, snackbars
 */
sealed interface CharacterSideEffect {
    /**
     * Show a toast or snackbar with a message
     */
    data class ShowToast(val message: String) : CharacterSideEffect
    
    /**
     * Navigate to character details screen
     */
    data class NavigateToDetails(val characterId: Int) : CharacterSideEffect
    
    /**
     * Show a generic error dialog
     */
    data class ShowError(val message: String) : CharacterSideEffect
}
```

**Key Points:**
- Side effects are consumed once
- Use for navigation, toasts, dialogs
- Don't use for persistent UI state

---

### 4.4 ViewModel - State Management

### app/mvi/CharacterViewModel.kt
```kotlin
package com.android.rickandmortymvvm.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.rickandmortymvvm.data.repository.CharacterRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVI ViewModel for Character feature
 * 
 * MVI Flow:
 * 1. View sends Intent → ViewModel
 * 2. ViewModel processes Intent → Updates State
 * 3. View observes State → Renders UI
 * 4. Optional: ViewModel sends SideEffect → View handles
 */
class CharacterViewModel(
    private val repository: CharacterRepository = CharacterRepository()
) : ViewModel() {

    // State: Single source of truth for UI state
    private val _state = MutableStateFlow<CharacterState>(CharacterState.Idle)
    val state: StateFlow<CharacterState> = _state.asStateFlow()

    // Side Effects: One-time events channel
    private val _sideEffect = Channel<CharacterSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        // Auto-load characters on initialization
        handleIntent(CharacterIntent.LoadCharacters)
    }

    /**
     * Main entry point for all user intentions
     */
    fun handleIntent(intent: CharacterIntent) {
        when (intent) {
            is CharacterIntent.LoadCharacters -> loadCharacters()
            is CharacterIntent.RefreshCharacters -> refreshCharacters()
            is CharacterIntent.RetryLoadCharacters -> retryLoadCharacters()
        }
    }

    /**
     * Load characters for the first time
     */
    private fun loadCharacters() {
        viewModelScope.launch {
            // Set loading state
            _state.update { CharacterState.Loading }

            try {
                // Fetch characters from repository
                val characters = repository.getCharacters()
                
                // Update to success state
                _state.update {
                    CharacterState.Success(characters = characters)
                }
            } catch (e: Exception) {
                // Update to error state
                _state.update {
                    CharacterState.Error(
                        message = e.message ?: "Unknown error occurred"
                    )
                }
                
                // Send side effect to show error
                _sideEffect.send(
                    CharacterSideEffect.ShowError("Failed to load characters")
                )
            }
        }
    }

    /**
     * Refresh characters while keeping existing data visible
     */
    private fun refreshCharacters() {
        viewModelScope.launch {
            // Get current characters if available
            val currentCharacters = when (val currentState = _state.value) {
                is CharacterState.Success -> currentState.characters
                is CharacterState.Error -> currentState.previousCharacters
                else -> emptyList()
            }

            // Set refreshing state
            _state.update {
                CharacterState.Success(
                    characters = currentCharacters,
                    isRefreshing = true
                )
            }

            try {
                // Fetch fresh characters
                val characters = repository.getCharacters()
                
                // Update with fresh data
                _state.update {
                    CharacterState.Success(
                        characters = characters,
                        isRefreshing = false
                    )
                }
                
                // Send success side effect
                _sideEffect.send(
                    CharacterSideEffect.ShowToast("Characters refreshed successfully")
                )
            } catch (e: Exception) {
                // Revert to previous state
                if (currentCharacters.isNotEmpty()) {
                    _state.update {
                        CharacterState.Success(
                            characters = currentCharacters,
                            isRefreshing = false
                        )
                    }
                    _sideEffect.send(
                        CharacterSideEffect.ShowToast("Failed to refresh")
                    )
                } else {
                    _state.update {
                        CharacterState.Error(
                            message = e.message ?: "Unknown error occurred"
                        )
                    }
                }
            }
        }
    }

    /**
     * Retry loading characters after an error
     */
    private fun retryLoadCharacters() {
        loadCharacters()
    }
}
```

**Key Points:**
- Use `StateFlow` for state (hot stream, always has value)
- Use `Channel` for side effects (cold stream, consumed once)
- `handleIntent()` is the single entry point
- All state changes are explicit and traceable
- Use `update {}` for atomic state updates

---

### 4.5 View - UI Layer

### app/mvi/CharacterScreen.kt
```kotlin
package com.android.rickandmortymvvm.mvi

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
 * 4. Handle side effects
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CharacterScreen(
    viewModel: CharacterViewModel,
    modifier: Modifier = Modifier
) {
    // 1. Observe state
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // 2. Handle side effects
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
                }
            }
        }
    }

    // 3. Render UI based on state
    when (val currentState = state) {
        is CharacterState.Idle -> IdleContent()
        is CharacterState.Loading -> LoadingContent()
        is CharacterState.Success -> {
            val pullRefreshState = rememberPullRefreshState(
                refreshing = currentState.isRefreshing,
                onRefresh = {
                    // Send refresh intent
                    viewModel.handleIntent(CharacterIntent.RefreshCharacters)
                }
            )

            Box(modifier.fillMaxSize().pullRefresh(pullRefreshState)) {
                CharacterListContent(currentState.characters)
                PullRefreshIndicator(
                    refreshing = currentState.isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
        is CharacterState.Error -> {
            ErrorContent(
                message = currentState.message,
                onRetry = {
                    // Send retry intent
                    viewModel.handleIntent(CharacterIntent.RetryLoadCharacters)
                }
            )
        }
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
private fun CharacterListContent(characters: List<Character>) {
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
            CharacterCard(character)
        }
    }
}

@Composable
private fun CharacterCard(character: Character) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clickable { /* Handle click */ },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = character.image,
                contentDescription = character.name,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Status: ${character.status}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(text = "⚠️", style = MaterialTheme.typography.displayLarge)
            Text(
                text = "Oops! Something went wrong",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun IdleContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Ready to load characters")
    }
}
```

**Key Points:**
- Observe state with `collectAsState()`
- Handle side effects in `LaunchedEffect`
- Send intents on user actions
- Use exhaustive when expression for states
- Separate composables for each state

---

## 5. MainActivity

### MainActivity.kt
```kotlin
package com.android.rickandmortymvvm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.rickandmortymvvm.mvi.CharacterScreen
import com.android.rickandmortymvvm.mvi.CharacterViewModel
import com.android.rickandmortymvvm.ui.theme.RickandMortyMVVMTheme

/**
 * Main Activity using MVI Architecture
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CharacterViewModel = viewModel()

            RickandMortyMVVMTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CharacterScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
```

**Key Points:**
- Create ViewModel using `viewModel()` factory
- Pass ViewModel to screen composable
- Use Scaffold for proper Material Design layout

---

## 6. Android Manifest

### AndroidManifest.xml
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- Required for network requests -->
    <uses-permission android:name="android.permission.INTERNET" />
    
    <application
        android:name=".MainApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.RickandMortyMVVM">
        
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.RickandMortyMVVM">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

---

## MVI Architecture Summary

### Data Flow Diagram
```
User Action (Swipe/Tap)
        ↓
Intent Sent to ViewModel
        ↓
ViewModel Processes Intent
        ↓
State Updated (Immutable)
        ↓
UI Observes State Change
        ↓
UI Re-renders
        ↓
(Optional) Side Effect Emitted
        ↓
UI Handles Side Effect
```

### Key Principles

1. **Unidirectional Data Flow**
   - Data flows in one direction only
   - Intent → ViewModel → State → View

2. **Single Source of Truth**
   - One state represents entire UI
   - No conflicting states possible

3. **Immutability**
   - States never modified, only replaced
   - Use `update {}` for atomic changes

4. **Type Safety**
   - Sealed interfaces ensure compile-time safety
   - Exhaustive when expressions

5. **Separation of Concerns**
   - Clear boundaries between layers
   - Easy to test and maintain

### MVI vs MVVM

| Aspect | MVI | MVVM |
|--------|-----|------|
| State Management | Single immutable state | Multiple observable properties |
| Data Flow | Unidirectional | Bidirectional |
| State Conflicts | Impossible | Possible |
| Side Effects | Explicit channel | Mixed with state |
| Testability | Excellent | Good |
| Boilerplate | Medium | Low |

---

## Testing (Optional)

### Unit Test Example
```kotlin
@Test
fun `should transition from Loading to Success when data loads`() = runTest {
    // Given
    val mockCharacters = listOf(
        Character(1, "Rick", "Alive", "url1"),
        Character(2, "Morty", "Alive", "url2")
    )
    coEvery { repository.getCharacters() } returns mockCharacters
    
    val viewModel = CharacterViewModel(repository)
    
    // When
    viewModel.handleIntent(CharacterIntent.LoadCharacters)
    
    // Then
    viewModel.state.test {
        assertEquals(CharacterState.Idle, awaitItem())
        assertEquals(CharacterState.Loading, awaitItem())
        val success = awaitItem() as CharacterState.Success
        assertEquals(mockCharacters, success.characters)
    }
}
```

---

## Common Pitfalls to Avoid

### ❌ Bad: Exposing Mutable State
```kotlin
val state: MutableStateFlow<State> = MutableStateFlow(State.Idle)
```

### ✅ Good: Expose Immutable State
```kotlin
private val _state = MutableStateFlow<State>(State.Idle)
val state: StateFlow<State> = _state.asStateFlow()
```

### ❌ Bad: Multiple State Updates
```kotlin
_state.update { it.copy(loading = false) }
_state.update { it.copy(data = newData) }
```

### ✅ Good: Single Atomic Update
```kotlin
_state.update { State.Success(newData) }
```

### ❌ Bad: Side Effects in State
```kotlin
data class State(
    val data: List<Item>,
    val navigateToDetails: Int? = null // ❌ Survives config changes
)
```

### ✅ Good: Separate Side Effects
```kotlin
sealed interface SideEffect {
    data class NavigateToDetails(val id: Int) : SideEffect
}
```

---

## Best Practices

1. **Keep States Mutually Exclusive** - Use sealed interfaces
2. **Use Meaningful Intent Names** - Clear user actions
3. **Keep ViewModels Pure** - No Android dependencies
4. **Use Side Effects for One-Time Events** - Navigation, toasts
5. **Make State Updates Atomic** - Single update operation
6. **Handle All States in UI** - Exhaustive when expressions
7. **Test State Transitions** - Unit test all paths

---

## Resources

- **Rick and Morty API**: https://rickandmortyapi.com
- **Kotlin Flow**: https://kotlinlang.org/docs/flow.html
- **Jetpack Compose**: https://developer.android.com/jetpack/compose
- **MVI Pattern**: https://hannesdorfmann.com/android/model-view-intent/

---

**✅ MVI Architecture Complete!**

This document provides a complete guide to building Android apps with MVI architecture using the Rick and Morty API as a practical example.
