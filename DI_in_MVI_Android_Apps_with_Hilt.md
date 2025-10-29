# Dependency Injection in MVI Android Apps with Hilt

## Introduction to MVI and the Challenge of Dependency Management

The Model-View-Intent (MVI) architectural pattern has gained significant traction in Android development for its unidirectional data flow, clear state management, and enhanced testability. In an MVI application, the user's intentions (Intents) are processed by a ViewModel, which then updates the application's state (Model), and finally, the View renders this state. This reactive approach makes MVI robust and predictable.

However, as with any complex application, managing dependencies can become a significant challenge. Components often rely on other components, such as API services, repositories, or utility classes. Manually creating and providing these dependencies throughout your application can lead to:

*   **Boilerplate Code:** Repetitive instantiation of objects.
*   **Tight Coupling:** Components become heavily reliant on concrete implementations, hindering testability and flexibility.
*   **Difficulty in Testing:** Mocking dependencies for unit tests becomes cumbersome.

This is where Dependency Injection (DI) frameworks like Hilt come into play. Hilt provides a standardized way to incorporate DI into your Android application, leveraging the power of Dagger.

## Why Hilt for MVI?

Integrating Hilt into an MVI architecture offers several compelling advantages:

1.  **Simplified Dependency Provision:** Hilt automates much of the boilerplate associated with Dagger, making it easier to provide dependencies to your ViewModels, Activities, Fragments, and other Android classes.
2.  **Improved Testability:** By injecting dependencies, you can easily swap out real implementations with mock objects during testing, ensuring that your tests are isolated and reliable.
3.  **Clearer Architecture:** Hilt helps enforce a cleaner separation of concerns by explicitly defining how dependencies are provided and consumed. This aligns well with MVI's goal of a well-structured and understandable codebase.
4.  **Lifecycle-Aware Components:** Hilt integrates seamlessly with Android's lifecycle, ensuring that dependencies are correctly scoped and managed, preventing memory leaks and other common issues.

Let's walk through the process of integrating Hilt into an existing MVI Android application.

## Step-by-Step Hilt Integration in an MVI App

We'll assume you have an existing MVI project structure similar to the following:

*   `MainActivity.kt`: The main entry point of the application.
*   `CharacterViewModel.kt`: The ViewModel responsible for handling intents and managing state.
*   `CharacterRepository.kt`: The repository for fetching data.
*   `ApiClient.kt`: An object responsible for creating the Retrofit API service.
*   `RickandMortyApi.kt`: The Retrofit interface for API calls.

### 1. Add Hilt Dependencies

First, you need to add the Hilt plugin and dependencies to your project's `build.gradle.kts` files.

**Project-level `build.gradle.kts`:**

```kotlin
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false // Add this line
}
```

**App-level `build.gradle.kts`:**

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.2.0"
    kotlin("kapt") // Add this line
    alias(libs.plugins.hilt) // Add this line
}

// ... other configurations ...

dependencies {
    // ... existing dependencies ...

    // Hilt for dependency injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // ... other dependencies ...
}
```

**`gradle/libs.versions.toml`:**

```toml
[versions]
agp = "8.12.0"
hilt = "2.51.1" # Add this line
kotlin = "2.2.0"
# ... other versions ...

[libraries]
# ... existing libraries ...
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }

[plugins]
# ... existing plugins ...
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
```

### 2. Create a Hilt Application Class

Hilt needs to know where to generate its dependency graph. You do this by annotating your `Application` class with `@HiltAndroidApp`. If you don't have a custom `Application` class, create one:

```kotlin
// RickandMortyMVVMApp.kt
package com.android.rickandmortymvvm

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RickandMortyMVVMApp : Application()
```

### 3. Update `AndroidManifest.xml`

Tell Android to use your new Hilt-enabled application class:

```xml
<application
    android:name=".RickandMortyMVVMApp" // Add this line
    android:allowBackup="true"
    android:dataExtractionRules="@xml/data_extraction_rules"
    android:fullBackupContent="@xml/backup_rules"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:roundIcon="@mipmap/ic_launcher_round"
    android:supportsRtl="true"
    android:theme="@style/Theme.RickandMortyMVVM">
    <!-- ... activities ... -->
</application>
```

### 4. Create Hilt Modules for Dependencies

Now, let's define how Hilt should provide our `RickandMortyApi` and `CharacterRepository`. We'll create an `AppModule` object:

```kotlin
// AppModule.kt
package com.android.rickandmortymvvm.di

import com.android.rickandmortymvvm.data.network.RickandMortyApi
import com.android.rickandmortymvvm.data.repository.CharacterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
        }
    }

    @Singleton
    @Provides
    fun provideRickandMortyApi(json: Json): RickandMortyApi {
        val BASE_URL = "https://rickandmortyapi.com/api/"
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(RickandMortyApi::class.java)
    }

    @Singleton
    @Provides
    fun provideCharacterRepository(api: RickandMortyApi): CharacterRepository {
        return CharacterRepository(api)
    }
}
```

Notice that we've moved the Retrofit setup into the `AppModule` and made `Json` injectable as well. The `ApiClient` object is no longer needed and can be deleted.

### 5. Inject Dependencies into `CharacterRepository` and `CharacterViewModel`

Now, we can use constructor injection for our `CharacterRepository` and `CharacterViewModel`.

**`CharacterRepository.kt`:**

```kotlin
package com.android.rickandmortymvvm.data.repository

import com.android.rickandmortymvvm.data.model.Character
import com.android.rickandmortymvvm.data.network.RickandMortyApi
import javax.inject.Inject // Add this import

class CharacterRepository @Inject constructor(private val api: RickandMortyApi) { // Add @Inject and remove default parameter
    suspend fun getCharacters(): List<Character> {
        return try {
            api.getCharacters().results
        }
        catch (e: Exception) {
            throw Exception("Failed to fetch characters ${e.message}")
        }
    }
}
```

**`CharacterViewModel.kt`:**

```kotlin
package com.android.rickandmortymvvm.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.rickandmortymvvm.data.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel // Add this import
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject // Add this import

@HiltViewModel // Add this annotation
class CharacterViewModel @Inject constructor( // Add @Inject and remove default parameter
    private val repository: CharacterRepository
) : ViewModel() {
    // ... ViewModel implementation ...
}
```

### 6. Use `hiltViewModel()` in `MainActivity`

Finally, in your `MainActivity`, you can now obtain the `CharacterViewModel` using `hiltViewModel()`:

```kotlin
package com.android.rickandmortymvvm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.rickandmortymvvm.mvi.CharacterScreen
import com.android.rickandmortymvvm.mvi.CharacterViewModel
import com.android.rickandmortymvvm.ui.theme.RickandMortyMVVMTheme
import dagger.hilt.android.AndroidEntryPoint // Add this import
import androidx.hilt.navigation.compose.hiltViewModel // Add this import

/**
 * Main Activity using MVI Architecture
 * 
 * MVI Flow:
 * Activity → CharacterScreen (View) → CharacterViewModel → CharacterState
 */
@AndroidEntryPoint // Add this annotation
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Obtain ViewModel using hiltViewModel()
            val viewModel: CharacterViewModel = hiltViewModel() // Change this line

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

## Benefits and Conclusion

By integrating Hilt into your MVI Android application, you achieve:

*   **Decoupled Components:** Your `CharacterViewModel` no longer directly instantiates `CharacterRepository`, and `CharacterRepository` no longer directly instantiates `RickandMortyApi`. Hilt handles the creation and provision of these dependencies.
*   **Easier Testing:** You can now easily provide mock implementations of `CharacterRepository` or `RickandMortyApi` during unit tests for your `CharacterViewModel`, making your tests more focused and reliable.
*   **Maintainable Codebase:** The explicit declaration of dependencies through Hilt modules and annotations makes the codebase easier to understand and maintain as it grows.

Hilt significantly streamlines dependency injection in Android, and its integration with MVI further enhances the architectural benefits of both patterns. This combination leads to more robust, testable, and maintainable Android applications.
