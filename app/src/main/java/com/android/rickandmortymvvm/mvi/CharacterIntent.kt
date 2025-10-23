package com.android.rickandmortymvvm.mvi

/**
 * Represents all possible user intentions/actions in the Character feature
 * Each sealed class represents a distinct user action
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
