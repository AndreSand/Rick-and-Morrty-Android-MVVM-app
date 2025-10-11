package com.android.rickandmortymvvm.data.repository

import com.android.rickandmortymvvm.data.mapper.ProtoMapper.toDomain
import com.android.rickandmortymvvm.data.model.Character
import com.android.rickandmortymvvm.data.network.ApiClient
import com.android.rickandmortymvvm.data.network.RickandMortyApi
import com.android.rickandmortymvvm.data.network.RickandMortyProtoApi

/**
 * Repository for fetching Rick and Morty characters
 * Supports both JSON and Protocol Buffers formats
 */
class CharacterRepository(
    private val jsonApi: RickandMortyApi = ApiClient.api,
    private val protoApi: RickandMortyProtoApi = ApiClient.protoApi
) {
    
    /**
     * Fetch characters using JSON format (existing)
     */
    suspend fun getCharacters(): List<Character> {
        return try {
            jsonApi.getCharacters().results
        } catch (e: Exception) {
            throw Exception("Failed to fetch characters: ${e.message}")
        }
    }
    
    /**
     * Fetch characters using Protocol Buffers format (new)
     * Benefits:
     * - 70-90% smaller payload size
     * - 3-10x faster serialization
     * - Better battery efficiency
     */
    suspend fun getCharactersProto(): List<Character> {
        return try {
            protoApi.getCharactersProto().toDomain().results
        } catch (e: Exception) {
            throw Exception("Failed to fetch characters with Protobuf: ${e.message}")
        }
    }
    
    /**
     * Fetch characters with automatic fallback
     * Tries Protobuf first, falls back to JSON if it fails
     */
    suspend fun getCharactersWithFallback(): List<Character> {
        return try {
            // Try Protobuf first for better performance
            getCharactersProto()
        } catch (protoError: Exception) {
            try {
                // Fallback to JSON if Protobuf fails
                getCharacters()
            } catch (jsonError: Exception) {
                throw Exception("Failed to fetch characters with both formats: Protobuf(${protoError.message}), JSON(${jsonError.message})")
            }
        }
    }
}
