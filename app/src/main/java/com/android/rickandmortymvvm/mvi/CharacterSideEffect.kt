package com.android.rickandmortymvvm.mvi

/**
 * Represents one-time side effects that should not be part of the state
 * Examples: navigation, showing toasts, showing snackbars
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
