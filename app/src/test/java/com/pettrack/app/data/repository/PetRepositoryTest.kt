package com.pettrack.app.data.repository

import com.pettrack.app.data.remote.dto.PetDto
import com.pettrack.app.domain.model.PetInput
import com.pettrack.app.domain.model.PetStatus
import com.pettrack.app.fakes.FakePetApi
import com.pettrack.app.fakes.FakeRpcApi
import com.pettrack.app.fakes.FakeSessionStore
import com.pettrack.app.fakes.FakeStorageApi
import com.pettrack.app.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class PetRepositoryTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val petApi = FakePetApi()
    private val rpcApi = FakeRpcApi()
    private val storageApi = FakeStorageApi()
    private val session = FakeSessionStore().apply { userId = "u1" }

    private fun repository() = PetRepository(petApi, rpcApi, storageApi, session, mainRule.dispatcher)

    private val createdRow = PetDto(id = "pet-9", ownerId = "u1", name = "Toby", species = "perro", status = "perdida")

    private fun input() = PetInput(
        name = "Toby", species = "perro", breed = null, approxAge = null, color = null,
        size = null, distinguishingMarks = null, hasCollarChip = false, chipNumber = null,
        status = PetStatus.PERDIDA, lostAt = null,
    )

    @Test
    fun createPet_returnsId_andDoesNotRollBack_onSuccess() = runTest {
        petApi.pets = listOf(createdRow)
        val result = repository().createPet(input(), 9.0, -79.5, null)
        assertTrue(result.isSuccess)
        assertEquals("pet-9", result.getOrNull())
        assertTrue("No rollback expected on success", petApi.deletedIds.isEmpty())
    }

    @Test
    fun createPet_rollsBackCreatedRow_whenSetLocationFails() = runTest {
        petApi.pets = listOf(createdRow)
        rpcApi.setLocationResponse =
            Response.error(403, "{\"message\":\"rls\"}".toResponseBody("application/json".toMediaType()))

        val result = repository().createPet(input(), 9.0, -79.5, null)

        assertTrue("A post-insert failure must surface as failure", result.isFailure)
        // The already-committed row must be deleted so a retry can't create a duplicate.
        assertTrue("Expected rollback delete of the created row", petApi.deletedIds.contains("eq.pet-9"))
    }
}
