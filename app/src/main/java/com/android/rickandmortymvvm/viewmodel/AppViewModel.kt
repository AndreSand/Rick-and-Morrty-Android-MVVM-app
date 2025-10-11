package com.android.rickandmortymvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.rickandmortymvvm.data.model.Character
import com.android.rickandmortymvvm.data.repository.CharacterRepository
import com.android.rickandmortymvvm.util.NetworkConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppUiState(
    val apps: List<Character> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val dataFormat: NetworkConfig.DataFormat = NetworkConfig.currentFormat,
    val bytesReceived: Long? = null  // Track payload size for comparison
)

class AppViewModel(private val repository: CharacterRepository = CharacterRepository()) :
    ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        fetchCharacters()
    }

    /**
     * Fetch characters using auto-fallback (Protobuf first, then JSON)
     * This is the recommended approach for production
     */
    private fun fetchCharacters() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val apps = repository.getCharactersWithFallback()
                _uiState.value = _uiState.value.copy(
                    apps = apps, 
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message, 
                    isLoading = false
                )
            }
        }
    }
    
    /**
     * Fetch characters using JSON format explicitly
     */
    fun fetchCharactersJson() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                dataFormat = NetworkConfig.DataFormat.JSON
            )

            try {
                val apps = repository.getCharacters()
                _uiState.value = _uiState.value.copy(
                    apps = apps, 
                    isLoading = false,
                    error = null,
                    dataFormat = NetworkConfig.DataFormat.JSON
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "JSON Error: ${e.message}", 
                    isLoading = false
                )
            }
        }
    }
    
    /**
     * Fetch characters using Protobuf format explicitly
     */
    fun fetchCharactersProtobuf() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                dataFormat = NetworkConfig.DataFormat.PROTOBUF
            )

            try {
                val apps = repository.getCharactersProto()
                _uiState.value = _uiState.value.copy(
                    apps = apps, 
                    isLoading = false,
                    error = null,
                    dataFormat = NetworkConfig.DataFormat.PROTOBUF
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Protobuf Error: ${e.message}", 
                    isLoading = false
                )
            }
        }
    }
    
    /**
     * Refresh data
     */
    fun refresh() {
        fetchCharacters()
    }
    
    /**
     * Switch data format and reload
     */
    fun switchDataFormat(format: NetworkConfig.DataFormat) {
        NetworkConfig.currentFormat = format
        when (format) {
            NetworkConfig.DataFormat.JSON -> fetchCharactersJson()
            NetworkConfig.DataFormat.PROTOBUF -> fetchCharactersProtobuf()
            NetworkConfig.DataFormat.AUTO_FALLBACK -> fetchCharacters()
        }
    }
}
