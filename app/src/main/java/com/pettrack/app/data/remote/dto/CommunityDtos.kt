package com.pettrack.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Row from the pets_nearby RPC. */
@Serializable
data class PetNearbyDto(
    val id: String,
    @SerialName("owner_id") val ownerId: String,
    val name: String,
    val species: String,
    val breed: String? = null,
    val status: String,
    val latitude: Double,
    val longitude: Double,
    @SerialName("distance_m") val distanceM: Double,
    @SerialName("primary_photo_path") val primaryPhotoPath: String? = null,
    @SerialName("lost_at") val lostAt: String? = null,
)

/** Body for POST /rest/v1/rpc/pets_nearby (keys match the SQL parameter names). */
@Serializable
data class PetsNearbyRequest(
    val lat: Double,
    val lng: Double,
    @SerialName("radius_m") val radiusM: Double,
    @SerialName("p_species") val species: String? = null,
    @SerialName("p_status") val status: String? = null,
    @SerialName("p_from") val from: String? = null,
    @SerialName("p_to") val to: String? = null,
)

/** Row from the sightings_geo view. */
@Serializable
data class SightingDto(
    val id: String,
    @SerialName("pet_id") val petId: String,
    @SerialName("reporter_id") val reporterId: String? = null,
    val note: String? = null,
    @SerialName("sighted_at") val sightedAt: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
)

/** Body for POST /rest/v1/rpc/report_sighting. */
@Serializable
data class ReportSightingRequest(
    @SerialName("p_pet_id") val petId: String,
    val lat: Double,
    val lng: Double,
    val note: String? = null,
)

/** Body for POST /rest/v1/rpc/get_owner_contact. */
@Serializable
data class OwnerContactRequest(
    @SerialName("p_pet_id") val petId: String,
)

@Serializable
data class OwnerContactDto(
    @SerialName("full_name") val fullName: String? = null,
    val phone: String? = null,
    val email: String? = null,
)

/** Read shape for pet_photos rows. */
@Serializable
data class PetPhotoDto(
    val id: String,
    @SerialName("pet_id") val petId: String,
    @SerialName("storage_path") val storagePath: String,
    @SerialName("is_primary") val isPrimary: Boolean = false,
)
