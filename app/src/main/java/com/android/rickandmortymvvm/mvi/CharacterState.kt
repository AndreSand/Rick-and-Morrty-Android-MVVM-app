package com.android.rickandmortymvvm.mvi

import com.android.rickandmortymvvm.data.model.Character

/**
 * Represents all possible UI states for the Character feature
 * Using sealed interface for exhaustive when expressions
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
     * @param characters List of characters to display
     * @param isRefreshing Whether we're refreshing while showing data
     */
    data class Success(
        val characters: List<Character>,
        val isRefreshing: Boolean = false
    ) : CharacterState
    
    /**
     * Error state with error message
     * @param message Error message to display
     * @param previousCharacters Previously loaded characters (if any) to show during error
     */
    data class Error(
        val message: String,
        val previousCharacters: List<Character> = emptyList()
    ) : CharacterState
}
