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
 * 4. Optional: ViewModel sends SideEffect → View handles one-time events
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
     * Processes intents and updates state accordingly
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
                    CharacterSideEffect.ShowError(
                        message = "Failed to load characters"
                    )
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
                // Revert to previous success state or show error
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
        // Simply delegate to loadCharacters
        loadCharacters()
    }
}
