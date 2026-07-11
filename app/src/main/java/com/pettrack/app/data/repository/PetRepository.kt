package com.pettrack.app.data.repository

import com.pettrack.app.core.di.IoDispatcher
import com.pettrack.app.core.session.SessionStore
import com.pettrack.app.data.remote.api.PetApi
import com.pettrack.app.data.remote.api.RpcApi
import com.pettrack.app.data.remote.api.StorageApi
import com.pettrack.app.data.remote.dto.PetDto
import com.pettrack.app.data.remote.dto.PetInsertDto
import com.pettrack.app.data.remote.dto.PetPatchDto
import com.pettrack.app.data.remote.dto.PetPhotoInsert
import com.pettrack.app.data.remote.dto.PetPutDto
import com.pettrack.app.data.remote.dto.SetLocationRequest
import com.pettrack.app.domain.model.Pet
import com.pettrack.app.domain.model.PetInput
import com.pettrack.app.domain.model.PetSize
import com.pettrack.app.domain.model.PetStatus
import com.pettrack.app.domain.model.PhotoBytes
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetRepository @Inject constructor(
    private val petApi: PetApi,
    private val rpcApi: RpcApi,
    private val storageApi: StorageApi,
    private val session: SessionStore,
    @IoDispatcher private val io: CoroutineDispatcher,
) {

    suspend fun myPets(): Result<List<Pet>> = runCatching {
        withContext(io) {
            val uid = session.userId ?: error("Sesión no iniciada")
            petApi.getPets(ownerIdEq = "eq.$uid").map { it.toDomain() }
        }
    }

    suspend fun getPet(id: String): Result<Pet> = runCatching {
        withContext(io) {
            petApi.getPet("eq.$id").firstOrNull()?.toDomain() ?: error("Mascota no encontrada")
        }
    }

    suspend fun createPet(input: PetInput, lat: Double?, lng: Double?, photo: PhotoBytes?): Result<String> =
        runCatching {
            withContext(io) {
                val uid = session.userId ?: error("Sesión no iniciada")
                val created = petApi.createPet(input.toInsert(uid)).firstOrNull()
                    ?: error("No se pudo crear la mascota")
                if (lat != null && lng != null) rpcApi.setPetLocation(SetLocationRequest(created.id, lat, lng))
                if (photo != null) uploadPhoto(uid, created.id, photo)
                created.id
            }
        }

    suspend fun updatePet(id: String, input: PetInput, lat: Double?, lng: Double?, photo: PhotoBytes?): Result<Unit> =
        runCatching {
            withContext(io) {
                val uid = session.userId ?: error("Sesión no iniciada")
                petApi.replacePet("eq.$id", input.toPut(id, uid))
                if (lat != null && lng != null) rpcApi.setPetLocation(SetLocationRequest(id, lat, lng))
                if (photo != null) uploadPhoto(uid, id, photo)
                Unit
            }
        }

    suspend fun setStatus(id: String, status: PetStatus): Result<Unit> = runCatching {
        withContext(io) {
            petApi.updatePet("eq.$id", PetPatchDto(status = status.api))
            Unit
        }
    }

    suspend fun deletePet(id: String): Result<Unit> = runCatching {
        withContext(io) {
            petApi.deletePet("eq.$id")
            Unit
        }
    }

    private suspend fun uploadPhoto(uid: String, petId: String, photo: PhotoBytes) {
        val path = "$uid/$petId/${UUID.randomUUID()}.${photo.ext}"
        storageApi.upload(path, photo.bytes.toRequestBody(photo.mime.toMediaType()), photo.mime)
        petApi.addPhoto(PetPhotoInsert(petId = petId, storagePath = path))
    }
}

private fun PetDto.toDomain() = Pet(
    id = id,
    ownerId = ownerId,
    name = name,
    species = species,
    breed = breed,
    approxAge = approxAge,
    color = color,
    size = PetSize.from(size),
    distinguishingMarks = distinguishingMarks,
    hasCollarChip = hasCollarChip,
    chipNumber = chipNumber,
    status = PetStatus.from(status),
    lostAt = lostAt,
    latitude = latitude,
    longitude = longitude,
)

private fun PetInput.toInsert(uid: String) = PetInsertDto(
    ownerId = uid,
    name = name,
    species = species,
    breed = breed,
    approxAge = approxAge,
    color = color,
    size = size?.api,
    distinguishingMarks = distinguishingMarks,
    hasCollarChip = hasCollarChip,
    chipNumber = chipNumber,
    status = status.api,
    lostAt = lostAt,
)

private fun PetInput.toPut(id: String, uid: String) = PetPutDto(
    id = id,
    ownerId = uid,
    name = name,
    species = species,
    breed = breed,
    approxAge = approxAge,
    color = color,
    size = size?.api,
    distinguishingMarks = distinguishingMarks,
    hasCollarChip = hasCollarChip,
    chipNumber = chipNumber,
    status = status.api,
    lostAt = lostAt,
)
