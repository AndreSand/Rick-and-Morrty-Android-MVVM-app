# Rick and Morty MVVM App

This is a simple Android app that displays a list of characters from the Rick and Morty API. It is built using the MVVM architecture pattern, with Jetpack Compose for the UI, and coroutines for asynchronous operations.

## MVVM Architecture

The app is structured into the following packages, which is a good implementation of the MVVM pattern:

*   **`data`**: Handles the data of the app. It contains the model, the repository, and the network layer.
*   **`ui`**: Contains the theme of the app.
*   **`view`**: Contains the UI of the app, which is built with Jetpack Compose.
*   **`viewmodel`**: Contains the ViewModel, which is responsible for holding and processing the data that is displayed in the UI.

### Data Layer (Model)

*   **`Character.kt`**: Defines the `Character` data model, representing a single Rick and Morty character with properties like `id`, `name`, `status`, and `image`. It also includes `CharacterResponse` to parse the list of characters from the API.
*   **`ApiService.kt`**: Handles the connection to the Rick and Morty API. It uses Retrofit to define the HTTP requests (e.g., `getCharacters`) and manages the base URL and JSON serialization.
*   **`CharacterRepository.kt`**: Acts as the single source of truth for character data. It fetches data from the `ApiService` and exposes it to the ViewModel. This isolates the data source from the rest of the app.

### UI Layer (View)

*   **`AppUIScreen.kt`**: This is a Composable function that defines the user interface. It observes the state from the `AppViewModel` and displays the list of characters using a `LazyColumn`. It also handles UI states like loading, error, and success.
*   **`MainActivity.kt`**: The main entry point of your app. It sets up the Jetpack Compose content and initializes the `AppViewModel`.
*   **`ui/theme/`**: This package contains your app's theme, including colors and typography.

### ViewModel Layer (ViewModel)

*   **`AppViewModel.kt`**: This class prepares and manages the data for the UI. It communicates with the `CharacterRepository` to fetch the characters, and it exposes the data to the UI through a `StateFlow` called `uiState`. It also manages the UI state, such as loading and error states, in the `AppUiState` data class.

### How they work together in MVVM:

1.  The **View** (`AppUIScreen.kt` and `MainActivity.kt`) is responsible for displaying the UI. It observes the `uiState` from the `AppViewModel`.
2.  The **ViewModel** (`AppViewModel.kt`) is notified when the View is created and starts fetching data from the `CharacterRepository`.
3.  The **Model** (`CharacterRepository.kt`, `ApiService.kt`, and `Character.kt`) handles the data operations. The `CharacterRepository` gets the data from the `ApiService`, which in turn makes the API call.
4.  When the data is ready, the `AppViewModel` updates its `uiState`.
5.  Because the View is observing the `uiState`, it automatically updates to display the new list of characters without any direct manipulation from the ViewModel.

This separation of concerns makes your app more modular, testable, and easier to maintain.

## How to run the app

1.  Clone this repository.
2.  Open the project in Android Studio.
3.  Run the app on an emulator or a physical device.
