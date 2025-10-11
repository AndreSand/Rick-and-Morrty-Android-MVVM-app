package com.android.rickandmortymvvm.data.network

import com.android.rickandmortymvvm.data.proto.CharacterProto
import retrofit2.http.GET

/**
 * Protobuf API interface for Rick and Morty API
 * Uses Protocol Buffers for efficient data transfer
 */
interface RickandMortyProtoApi {
    @GET("character")
    suspend fun getCharactersProto(): CharacterProto.CharacterResponse
}
