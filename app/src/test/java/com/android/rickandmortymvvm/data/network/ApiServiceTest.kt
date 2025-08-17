package com.android.rickandmortymvvm.data.network

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType

class ApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: RickandMortyApi
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(RickandMortyApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getCharacters returns correct data when API responds successfully`() = runTest {
        // Given
        val responseBody = """
            {
                "results": [
                    {
                        "id": 1,
                        "name": "Rick Sanchez",
                        "status": "Human",
                        "image": "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
                    },
                    {
                        "id": 2,
                        "name": "Morty Smith",
                        "status": "Human",
                        "image": "https://rickandmortyapi.com/api/character/avatar/2.jpeg"
                    }
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        // When
        val response = api.getCharacters()

        // Then
        assertEquals(2, response.results.size)
        assertEquals(1, response.results[0].id)
        assertEquals("Rick Sanchez", response.results[0].name)
        assertEquals("Human", response.results[0].status)
        assertEquals("https://rickandmortyapi.com/api/character/avatar/1.jpeg", response.results[0].image)
        assertEquals(2, response.results[1].id)
        assertEquals("Morty Smith", response.results[1].name)
        assertEquals("Human", response.results[1].status)
        assertEquals("https://rickandmortyapi.com/api/character/avatar/2.jpeg", response.results[1].image)
    }

    @Test
    fun `getCharacters returns empty list when API responds with empty results array`() = runTest {
        // Given
        val responseBody = """
            {
                "results": []
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        // When
        val response = api.getCharacters()

        // Then
        assertEquals(0, response.results.size)
    }

    @Test
    fun `getCharacters makes request to correct endpoint`() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"results": []}""")
                .addHeader("Content-Type", "application/json")
        )

        // When
        api.getCharacters()

        // Then
        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/character", request.path)
    }

    @Test(expected = Exception::class)
    fun `getCharacters throws exception when API returns error`() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        // When
        api.getCharacters()

        // Then - Exception should be thrown
    }

    @Test
    fun `ApiService object has correct base URL`() {
        // When
        val baseUrl = ApiClient.BASE_URL

        // Then
        assertEquals("https://rickandmortyapi.com/api/", baseUrl)
    }

    @Test
    fun `ApiService api property is not null`() {
        // When
        val apiInstance = ApiClient.api

        // Then
        assertNotNull(apiInstance)
    }

    @Test
    fun `getCharacters handles single character in response`() = runTest {
        // Given
        val responseBody = """
            {
                "results": [
                    {
                        "id": 1,
                        "name": "Rick Sanchez",
                        "status": "Human",
                        "image": "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
                    }
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        // When
        val response = api.getCharacters()

        // Then
        assertEquals(1, response.results.size)
        assertEquals("Rick Sanchez", response.results[0].name)
    }

    @Test
    fun `getCharacters handles missing fields with ignoreUnknownKeys`() = runTest {
        // Given
        val jsonWithExtraFields = """
            {
                "results": [
                    {
                        "id": 1,
                        "name": "Rick Sanchez",
                        "status": "Human",
                        "image": "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                        "gender": "Male",
                        "origin": {"name": "Earth"},
                        "location": {"name": "Earth"}
                    }
                ],
                "info": {
                    "count": 826,
                    "pages": 42,
                    "next": "https://rickandmortyapi.com/api/character?page=2",
                    "prev": null
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonWithExtraFields)
                .addHeader("Content-Type", "application/json")
        )

        // When
        val response = api.getCharacters()

        // Then
        assertEquals(1, response.results.size)
        assertEquals(1, response.results[0].id)
        assertEquals("Rick Sanchez", response.results[0].name)
        assertEquals("Human", response.results[0].status)
        assertEquals("https://rickandmortyapi.com/api/character/avatar/1.jpeg", response.results[0].image)
    }

    @Test
    fun `getCharacters handles different species correctly`() = runTest {
        // Given
        val responseBody = """
            {
                "results": [
                    {
                        "id": 1,
                        "name": "Rick Sanchez",
                        "status": "Human",
                        "image": "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
                    },
                    {
                        "id": 5,
                        "name": "Jerry Smith",
                        "status": "Human",
                        "image": "https://rickandmortyapi.com/api/character/avatar/5.jpeg"
                    },
                    {
                        "id": 3,
                        "name": "Summer Smith",
                        "status": "Human",
                        "image": "https://rickandmortyapi.com/api/character/avatar/3.jpeg"
                    }
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        // When
        val response = api.getCharacters()

        // Then
        assertEquals(3, response.results.size)
        response.results.forEach { character ->
            assertEquals("Human", character.status)
        }
    }
}
