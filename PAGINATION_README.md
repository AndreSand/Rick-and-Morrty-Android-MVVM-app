# Rick and Morty MVVM App with Pagination

This Android app demonstrates the implementation of **Pagination** using the **Paging 3 library** with **MVVM architecture** and **Jetpack Compose**. The app loads characters from the Rick and Morty API with efficient pagination that loads 20 items at a time.

## 🚀 Features

- **Paging 3 Integration**: Efficient loading of data with automatic pagination
- **MVVM Architecture**: Clean separation of concerns with ViewModel and Repository pattern
- **Jetpack Compose UI**: Modern declarative UI toolkit
- **Error Handling**: Comprehensive error states with retry functionality
- **Loading States**: Loading indicators for initial load and pagination
- **End Detection**: Clear indication when all data has been loaded
- **Unit Tests**: Comprehensive testing for pagination logic

## 📱 Screenshots & Pagination Behavior

The app demonstrates:
- **Initial Loading**: Shows loading indicator while fetching first page
- **Infinite Scroll**: Automatically loads more characters as user scrolls
- **Load More Indicator**: Shows loading spinner while fetching additional pages
- **Error Handling**: Retry button for failed requests
- **End State**: Displays message when all characters are loaded

## 🏗️ Architecture

```
app/
├── data/
│   ├── model/
│   │   └── Character.kt           # Data models with pagination info
│   ├── network/
│   │   └── ApiService.kt          # Retrofit API with pagination support
│   ├── paging/
│   │   └── CharacterPagingSource.kt # Paging 3 source implementation
│   └── repository/
│       └── CharacterRepository.kt  # Repository with paging flow
├── viewmodel/
│   └── AppViewModel.kt            # ViewModel exposing paging flow
└── view/
    └── AppUIScreen.kt             # Compose UI with pagination
```

## 🔧 Pagination Implementation Details

### 1. Dependencies Added
```kotlin
// Paging 3 library
implementation("androidx.paging:paging-runtime-ktx:3.3.5")
implementation("androidx.paging:paging-compose:3.3.5")
```

### 2. Data Models Enhanced
```kotlin
@Serializable
data class CharacterResponse(
    val results: List<Character>,
    val info: Info  // Added pagination metadata
)

@Serializable
data class Info(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)
```

### 3. API Service with Pagination
```kotlin
interface RickandMortyApi {
    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int): CharacterResponse
}
```

### 4. PagingSource Implementation
```kotlin
class CharacterPagingSource(private val api: RickandMortyApi) : PagingSource<Int, Character>() {
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        return try {
            val page = params.key ?: 1
            val response = api.getCharacters(page)
            
            LoadResult.Page(
                data = response.results,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.info.next == null) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
```

### 5. Repository with Paging
```kotlin
class CharacterRepository(private val api: RickandMortyApi = ApiService.api) {
    
    fun getCharactersPaginated(): Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20, // Rick and Morty API returns 20 items per page by default
                prefetchDistance = 2,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                CharacterPagingSource(api)
            }
        ).flow
    }
}
```

### 6. ViewModel with Cached Paging Flow
```kotlin
class AppViewModel(private val repository: CharacterRepository = CharacterRepository()) : ViewModel() {
    
    val charactersPagingFlow: Flow<PagingData<Character>> = repository
        .getCharactersPaginated()
        .cachedIn(viewModelScope)  // Important: Cache to survive configuration changes
}
```

### 7. Compose UI with LazyPagingItems
```kotlin
@Composable
fun MainScreen(viewModel: AppViewModel, modifier: Modifier) {
    val characters = viewModel.charactersPagingFlow.collectAsLazyPagingItems()

    LazyColumn {
        items(characters.itemCount) { index ->
            val character = characters[index]
            if (character != null) {
                CharacterItem(character = character)
            }
        }
        
        // Handle loading states
        when (characters.loadState.append) {
            is LoadState.Loading -> {
                item {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            is LoadState.Error -> {
                item {
                    ErrorItem(onRetry = { characters.retry() })
                }
            }
            is LoadState.NotLoading -> {
                if (characters.loadState.append.endOfPaginationReached) {
                    item {
                        Text("🎉 You've reached the end!")
                    }
                }
            }
        }
    }
}
```

## 📊 Pagination Configuration

### PagingConfig Parameters
- **pageSize**: 20 (matches Rick and Morty API default)
- **prefetchDistance**: 2 (loads next page when 2 items from end)
- **enablePlaceholders**: false (no placeholder items)
- **initialLoadSize**: 20 (initial load size same as page size)

### Load States Handled
- **LoadState.Loading**: Shows loading indicators
- **LoadState.Error**: Shows error with retry button
- **LoadState.NotLoading**: Normal state, checks for end of pagination

## 🧪 Testing

The app includes comprehensive unit tests for the pagination logic:

```kotlin
// Test successful page loading
@Test
fun `load returns Page when api call is successful`() = runTest {
    // Test implementation...
}

// Test error handling
@Test
fun `load returns Error when api call fails`() = runTest {
    // Test implementation...
}

// Test pagination keys
@Test
fun `load returns correct keys for middle page`() = runTest {
    // Test implementation...
}
```

## 🚀 Getting Started

1. **Clone the repository**
2. **Open in Android Studio**
3. **Sync Gradle dependencies**
4. **Run the app**

The app will automatically start loading Rick and Morty characters with pagination!

## 📚 Key Learning Points

### Paging 3 Benefits
- **Memory Efficiency**: Only loads data as needed
- **Network Efficiency**: Intelligent prefetching and caching
- **Automatic Retry**: Built-in error handling with retry mechanisms
- **Configuration Changes**: Survives screen rotations and process death
- **Compose Integration**: Seamless integration with LazyColumn

### Best Practices Implemented
- **Separation of Concerns**: Clean architecture with distinct layers
- **Error Handling**: Comprehensive error states with user-friendly messages
- **Loading States**: Clear feedback for all loading scenarios
- **Testing**: Unit tests for critical pagination logic
- **Performance**: Efficient rendering with minimal recomposition

## 🔗 API Reference

- **Rick and Morty API**: https://rickandmortyapi.com/
- **Endpoint Used**: `https://rickandmortyapi.com/api/character?page={page}`
- **Default Page Size**: 20 characters per page
- **Total Characters**: 826 characters across 42 pages

## 📖 References

- [Android Paging 3 Documentation](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)
- [Paging with Compose](https://developer.android.com/jetpack/compose/lists#large-datasets)
- [MVVM Architecture](https://developer.android.com/jetpack/guide)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)

---

**Made with ❤️ using Android Paging 3, MVVM, and Jetpack Compose**
