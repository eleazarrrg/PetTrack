package com.pettrack.app.data.repository

import com.pettrack.app.core.common.publicPhotoUrl
import com.pettrack.app.core.common.throwIfFailed
import com.pettrack.app.core.di.IoDispatcher
import com.pettrack.app.data.remote.api.PetApi
import com.pettrack.app.data.remote.api.RpcApi
import com.pettrack.app.data.remote.dto.OwnerContactDto
import com.pettrack.app.data.remote.dto.OwnerContactRequest
import com.pettrack.app.data.remote.dto.PetNearbyDto
import com.pettrack.app.data.remote.dto.PetsNearbyRequest
import com.pettrack.app.data.remote.dto.ReportSightingRequest
import com.pettrack.app.data.remote.dto.SightingDto
import com.pettrack.app.domain.model.NearbyPet
import com.pettrack.app.domain.model.OwnerContact
import com.pettrack.app.domain.model.PetStatus
import com.pettrack.app.domain.model.Sighting
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommunityRepository @Inject constructor(
    private val rpcApi: RpcApi,
    private val petApi: PetApi,
    @IoDispatcher private val io: CoroutineDispatcher,
) {

    suspend fun nearby(
        lat: Double,
        lng: Double,
        radiusM: Double,
        species: String?,
        status: PetStatus?,
        from: String?,
    ): Result<List<NearbyPet>> = runCatching {
        withContext(io) {
            rpcApi.petsNearby(
                PetsNearbyRequest(
                    lat = lat,
                    lng = lng,
                    radiusM = radiusM,
                    species = species?.trim()?.ifBlank { null },
                    status = status?.api,
                    from = from,
                ),
            ).map { it.toDomain() }
        }
    }

    suspend fun sightings(petId: String): Result<List<Sighting>> = runCatching {
        withContext(io) { petApi.getSightings("eq.$petId").map { it.toDomain() } }
    }

    suspend fun photoUrls(petId: String): Result<List<String>> = runCatching {
        withContext(io) { petApi.getPhotos("eq.$petId").map { publicPhotoUrl(it.storagePath) } }
    }

    suspend fun reportSighting(petId: String, lat: Double, lng: Double, note: String?): Result<Unit> =
        runCatching {
            withContext(io) {
                rpcApi.reportSighting(ReportSightingRequest(petId, lat, lng, note?.trim()?.ifBlank { null }))
                    .throwIfFailed()
                Unit
            }
        }

    suspend fun ownerContact(petId: String): Result<OwnerContact> = runCatching {
        withContext(io) {
            rpcApi.getOwnerContact(OwnerContactRequest(petId)).firstOrNull()?.toDomain()
                ?: OwnerContact(null, null, null)
        }
    }
}

private fun PetNearbyDto.toDomain() = NearbyPet(
    id = id,
    ownerId = ownerId,
    name = name,
    species = species,
    breed = breed,
    status = PetStatus.from(status),
    latitude = latitude,
    longitude = longitude,
    distanceM = distanceM,
    photoUrl = primaryPhotoPath?.let { publicPhotoUrl(it) },
    lostAt = lostAt,
)

private fun SightingDto.toDomain() = Sighting(
    id = id,
    petId = petId,
    note = note,
    sightedAt = sightedAt,
    latitude = latitude,
    longitude = longitude,
)

private fun OwnerContactDto.toDomain() = OwnerContact(
    fullName = fullName,
    phone = phone,
    email = email,
)
