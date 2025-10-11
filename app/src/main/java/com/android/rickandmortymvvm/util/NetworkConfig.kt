package com.android.rickandmortymvvm.util

/**
 * Configuration for network data format
 * Allows switching between JSON and Protocol Buffers
 */
object NetworkConfig {
    
    enum class DataFormat {
        JSON,           // Human-readable, slower, larger
        PROTOBUF,       // Binary, faster, smaller
        AUTO_FALLBACK   // Tries Protobuf first, falls back to JSON
    }
    
    /**
     * Current data format preference
     * Change this to switch between formats
     */
    var currentFormat: DataFormat = DataFormat.AUTO_FALLBACK
    
    /**
     * Helper function to check if Protobuf is enabled
     */
    fun isProtobufEnabled(): Boolean {
        return currentFormat == DataFormat.PROTOBUF || currentFormat == DataFormat.AUTO_FALLBACK
    }
    
    /**
     * Helper function to check if JSON is enabled
     */
    fun isJsonEnabled(): Boolean {
        return currentFormat == DataFormat.JSON || currentFormat == DataFormat.AUTO_FALLBACK
    }
}
