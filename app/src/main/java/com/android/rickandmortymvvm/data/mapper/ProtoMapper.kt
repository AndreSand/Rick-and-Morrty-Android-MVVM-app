package com.android.rickandmortymvvm.data.mapper

import com.android.rickandmortymvvm.data.model.Character
import com.android.rickandmortymvvm.data.model.CharacterResponse
import com.android.rickandmortymvvm.data.proto.CharacterProto

/**
 * Mapper class to convert between Protocol Buffer messages and domain models
 */
object ProtoMapper {
    
    /**
     * Convert Protobuf Character to domain Character
     */
    fun CharacterProto.Character.toDomain(): Character {
        return Character(
            id = this.id,
            name = this.name,
            status = this.status,
            image = this.image
        )
    }
    
    /**
     * Convert Protobuf CharacterResponse to domain CharacterResponse
     */
    fun CharacterProto.CharacterResponse.toDomain(): CharacterResponse {
        return CharacterResponse(
            results = this.resultsList.map { it.toDomain() }
        )
    }
    
    /**
     * Convert domain Character to Protobuf Character
     */
    fun Character.toProto(): CharacterProto.Character {
        return CharacterProto.Character.newBuilder()
            .setId(this.id)
            .setName(this.name)
            .setStatus(this.status)
            .setImage(this.image)
            .build()
    }
    
    /**
     * Convert domain CharacterResponse to Protobuf CharacterResponse
     */
    fun CharacterResponse.toProto(): CharacterProto.CharacterResponse {
        return CharacterProto.CharacterResponse.newBuilder()
            .addAllResults(this.results.map { it.toProto() })
            .build()
    }
}
