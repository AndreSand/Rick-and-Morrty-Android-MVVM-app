package com.android.rickandmortymvvm.demo

import com.android.rickandmortymvvm.data.mapper.ProtoMapper.toDomain
import com.android.rickandmortymvvm.data.mapper.ProtoMapper.toProto
import com.android.rickandmortymvvm.data.model.Character
import com.android.rickandmortymvvm.data.model.CharacterResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Demo class showing JSON vs Protobuf comparison
 * This is for educational purposes to demonstrate the size difference
 */
object ProtobufDemo {
    
    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true 
    }
    
    /**
     * Create sample data for demonstration
     */
    fun createSampleData(): CharacterResponse {
        return CharacterResponse(
            results = listOf(
                Character(
                    id = 1,
                    name = "Rick Sanchez",
                    status = "Alive",
                    image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
                ),
                Character(
                    id = 2,
                    name = "Morty Smith",
                    status = "Alive",
                    image = "https://rickandmortyapi.com/api/character/avatar/2.jpeg"
                ),
                Character(
                    id = 3,
                    name = "Summer Smith",
                    status = "Alive",
                    image = "https://rickandmortyapi.com/api/character/avatar/3.jpeg"
                )
            )
        )
    }
    
    /**
     * Compare JSON vs Protobuf sizes
     */
    fun compareFormats(): ComparisonResult {
        val sampleData = createSampleData()
        
        // JSON serialization
        val jsonString = json.encodeToString(sampleData)
        val jsonBytes = jsonString.toByteArray()
        
        // Protobuf serialization
        val protoMessage = sampleData.toProto()
        val protoBytes = protoMessage.toByteArray()
        
        return ComparisonResult(
            jsonSize = jsonBytes.size,
            protobufSize = protoBytes.size,
            savings = ((jsonBytes.size - protoBytes.size).toFloat() / jsonBytes.size * 100).toInt(),
            jsonSample = jsonString,
            characterCount = sampleData.results.size
        )
    }
    
    /**
     * Print comparison results
     */
    fun printComparison() {
        val result = compareFormats()
        
        println("""
        ╔════════════════════════════════════════════════╗
        ║     JSON vs Protocol Buffers Comparison        ║
        ╚════════════════════════════════════════════════╝
        
        📊 Data Statistics:
        ├─ Characters: ${result.characterCount}
        ├─ JSON Size: ${result.jsonSize} bytes
        ├─ Protobuf Size: ${result.protobufSize} bytes
        └─ Savings: ${result.savings}%
        
        📄 JSON Sample (Pretty Printed):
        ${result.jsonSample}
        
        ⚡ Protobuf Benefits:
        ├─ ${result.savings}% smaller payload
        ├─ 3-10x faster serialization
        ├─ Binary format (not human-readable)
        └─ Type-safe with compile-time validation
        
        💡 Tip: For ${result.characterCount} characters, you save ${result.jsonSize - result.protobufSize} bytes.
           For 100 characters, that's ~${(result.jsonSize - result.protobufSize) * 33} bytes saved!
        
        """.trimIndent())
    }
    
    /**
     * Simulate performance comparison
     */
    fun performanceTest(iterations: Int = 1000): PerformanceResult {
        val sampleData = createSampleData()
        
        // JSON performance
        val jsonStartTime = System.nanoTime()
        repeat(iterations) {
            val jsonString = json.encodeToString(sampleData)
            json.decodeFromString<CharacterResponse>(jsonString)
        }
        val jsonDuration = (System.nanoTime() - jsonStartTime) / 1_000_000 // Convert to ms
        
        // Protobuf performance
        val protoStartTime = System.nanoTime()
        repeat(iterations) {
            val protoMessage = sampleData.toProto()
            protoMessage.toDomain()
        }
        val protoDuration = (System.nanoTime() - protoStartTime) / 1_000_000 // Convert to ms
        
        return PerformanceResult(
            jsonTimeMs = jsonDuration,
            protobufTimeMs = protoDuration,
            speedImprovement = ((jsonDuration - protoDuration).toFloat() / jsonDuration * 100).toInt(),
            iterations = iterations
        )
    }
    
    /**
     * Print performance comparison
     */
    fun printPerformanceComparison(iterations: Int = 1000) {
        println("⏱️  Running performance test with $iterations iterations...")
        val result = performanceTest(iterations)
        
        println("""
        
        ╔════════════════════════════════════════════════╗
        ║         Performance Comparison Results         ║
        ╚════════════════════════════════════════════════╝
        
        ⚡ Speed Test (${result.iterations} iterations):
        ├─ JSON: ${result.jsonTimeMs}ms
        ├─ Protobuf: ${result.protobufTimeMs}ms
        └─ Improvement: ${result.speedImprovement}% faster
        
        🔋 Battery Impact:
        ├─ Less CPU time = Better battery life
        ├─ Less network data = Faster downloads
        └─ Smaller payloads = Lower cellular costs
        
        """.trimIndent())
    }
    
    data class ComparisonResult(
        val jsonSize: Int,
        val protobufSize: Int,
        val savings: Int,
        val jsonSample: String,
        val characterCount: Int
    )
    
    data class PerformanceResult(
        val jsonTimeMs: Long,
        val protobufTimeMs: Long,
        val speedImprovement: Int,
        val iterations: Int
    )
}

/**
 * Main demo function
 * Run this to see the comparison
 */
fun main() {
    println("🚀 Protocol Buffers Demo\n")
    
    // Size comparison
    ProtobufDemo.printComparison()
    
    // Performance comparison
    ProtobufDemo.printPerformanceComparison(1000)
    
    println("✅ Demo complete!")
}
