package com.pettrack.app.data.repository

import com.pettrack.app.fakes.FakePetApi
import com.pettrack.app.fakes.FakeRpcApi
import com.pettrack.app.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class CommunityRepositoryTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val rpcApi = FakeRpcApi()
    private val petApi = FakePetApi()

    private fun repository() = CommunityRepository(rpcApi, petApi, mainRule.dispatcher)

    @Test
    fun reportSighting_returnsSuccess_whenServerAccepts() = runTest {
        val result = repository().reportSighting("pet-1", 9.0, -79.5, "la vi en el parque")
        assertTrue(result.isSuccess)
    }

    @Test
    fun reportSighting_propagatesFailure_whenServerRejects() = runTest {
        rpcApi.reportSightingResponse =
            Response.error(400, "{\"message\":\"rejected\"}".toResponseBody("application/json".toMediaType()))

        val result = repository().reportSighting("pet-1", 9.0, -79.5, null)

        assertTrue("A server rejection must surface as a failed Result", result.isFailure)
        assertTrue(result.exceptionOrNull() is HttpException)
    }
}
