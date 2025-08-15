package com.android.rickandmortymvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.rickandmortymvvm.data.model.Character
import com.android.rickandmortymvvm.data.repository.CharacterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppUiState(
    val apps: List<Character> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class AppViewModel(private val repository: CharacterRepository = CharacterRepository()) :
    ViewModel() {

    val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        fetchCharacters()
    }

    private fun fetchCharacters() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val apps = repository.getCharacters()
                _uiState.value = _uiState.value.copy(
                    apps = apps, isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message, isLoading = false
                )
            }
        }
    }
}
