# Rick and Morty MVVM App with Navigation 3

This project demonstrates the implementation of **Jetpack Navigation 3** in a Rick and Morty themed Android app using the MVVM architecture pattern with Jetpack Compose.

## Features

- ✅ **Navigation 3 Implementation**: Modern, Compose-first navigation
- ✅ **Character List**: Browse Rick and Morty characters
- ✅ **Character Details**: Tap any character to view detailed information
- ✅ **MVVM Architecture**: Clean separation of concerns
- ✅ **API Integration**: Fetches data from Rick and Morty API
- ✅ **Type-Safe Navigation**: Serializable navigation routes
- ✅ **Modern UI**: Material 3 design with Jetpack Compose

## Navigation 3 Benefits

Navigation 3 offers several advantages over Navigation 2:

1. **Direct Back Stack Control**: You own and manage the back stack as a simple list
2. **Type Safety**: All navigation routes are type-safe with `@Serializable` data classes
3. **Compose-First**: Built specifically for Jetpack Compose
4. **Simpler State Management**: No complex NavController state management
5. **Better Testing**: Easier to test navigation logic

## Architecture

```
📱 App Structure
├── 🎯 MainActivity
├── 🧭 Navigation
│   ├── AppNavigation.kt (Routes)
│   └── AppNavigationHost.kt (Navigation Logic)
├── 🖼️ View (UI Components)
│   ├── AppUIScreen.kt (Character List)
│   └── CharacterDetailScreen.kt (Character Details)
├── 🏗️ ViewModel
│   └── AppViewModel.kt (Business Logic)
└── 📊 Data
    ├── model/ (Data Classes)
    ├── network/ (API Service)
    └── repository/ (Data Repository)
```

## Navigation Flow

1. **Character List** (`CharacterList`) - Default screen showing all characters
2. **Character Detail** (`CharacterDetail(characterId)`) - Detailed view for selected character

## How Navigation 3 Works

### 1. Define Routes
```kotlin
@Serializable
data object CharacterList : NavKey

@Serializable
data class CharacterDetail(val characterId: Int) : NavKey
```

### 2. Create Back Stack
```kotlin
val backStack = rememberNavBackStack<Any>(CharacterList)
```

### 3. Set Up Navigation
```kotlin
NavDisplay(
    backStack = backStack,
    entryProvider = entryProvider { 
        entry<CharacterList> { /* List Screen */ }
        entry<CharacterDetail> { destination -> 
            /* Detail Screen with destination.characterId */ 
        }
    }
)
```

### 4. Navigate
- **To Detail**: `backStack.add(CharacterDetail(characterId))`
- **Back**: `backStack.remove(destination)`

## Dependencies

```kotlin
// Navigation 3
implementation("androidx.navigation3:navigation3-runtime:1.0.0-alpha07")
implementation("androidx.navigation3:navigation3-ui:1.0.0-alpha07")

// Other key dependencies
implementation("io.coil-kt.coil3:coil-compose:3.3.0")
implementation("com.squareup.retrofit2:retrofit:3.0.0")
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
```

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Run the app
5. Tap on any character to see the navigation in action!

## Key Implementation Details

- **Stateful Navigation**: Navigation state persists across configuration changes
- **Type-Safe Arguments**: Character ID is passed safely through the route
- **Clean Back Stack Management**: Simple add/remove operations
- **MVVM Pattern**: ViewModel manages data, UI observes state changes
- **Reactive UI**: UI automatically updates when navigation state changes

## Migration from Navigation 2

If migrating from Navigation 2, key changes include:

1. Replace `NavController` with `NavBackStack`
2. Use `@Serializable` routes instead of string routes
3. Replace `NavHost` with `NavDisplay` and `entryProvider`
4. Direct back stack manipulation instead of `navigate()` calls

This implementation showcases the power and simplicity of Navigation 3 for modern Android development with Jetpack Compose.
