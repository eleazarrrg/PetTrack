package com.pettrack.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Read shape — from the `pets_geo` view (lat/lng projected as doubles). */
@Serializable
data class PetDto(
    val id: String,
    @SerialName("owner_id") val ownerId: String,
    val name: String,
    val species: String,
    val breed: String? = null,
    @SerialName("approx_age") val approxAge: String? = null,
    val color: String? = null,
    val size: String? = null,
    @SerialName("distinguishing_marks") val distinguishingMarks: String? = null,
    @SerialName("has_collar_chip") val hasCollarChip: Boolean = false,
    @SerialName("chip_number") val chipNumber: String? = null,
    val status: String,
    @SerialName("lost_at") val lostAt: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
)

/** POST /rest/v1/pets body (owner sets scalar fields; geo set separately via RPC). */
@Serializable
data class PetInsertDto(
    @SerialName("owner_id") val ownerId: String,
    val name: String,
    val species: String,
    val breed: String? = null,
    @SerialName("approx_age") val approxAge: String? = null,
    val color: String? = null,
    val size: String? = null,
    @SerialName("distinguishing_marks") val distinguishingMarks: String? = null,
    @SerialName("has_collar_chip") val hasCollarChip: Boolean = false,
    @SerialName("chip_number") val chipNumber: String? = null,
    val status: String,
    @SerialName("lost_at") val lostAt: String? = null,
)

/** PUT /rest/v1/pets full-row replace (single row upsert; includes id + owner). */
@Serializable
data class PetPutDto(
    val id: String,
    @SerialName("owner_id") val ownerId: String,
    val name: String,
    val species: String,
    val breed: String? = null,
    @SerialName("approx_age") val approxAge: String? = null,
    val color: String? = null,
    val size: String? = null,
    @SerialName("distinguishing_marks") val distinguishingMarks: String? = null,
    @SerialName("has_collar_chip") val hasCollarChip: Boolean = false,
    @SerialName("chip_number") val chipNumber: String? = null,
    val status: String,
    @SerialName("lost_at") val lostAt: String? = null,
)

/** PATCH /rest/v1/pets partial update (e.g. status toggle). Nulls omitted. */
@Serializable
data class PetPatchDto(
    val name: String? = null,
    val species: String? = null,
    val breed: String? = null,
    @SerialName("approx_age") val approxAge: String? = null,
    val color: String? = null,
    val size: String? = null,
    @SerialName("distinguishing_marks") val distinguishingMarks: String? = null,
    @SerialName("has_collar_chip") val hasCollarChip: Boolean? = null,
    @SerialName("chip_number") val chipNumber: String? = null,
    val status: String? = null,
    @SerialName("lost_at") val lostAt: String? = null,
)

/** Body for POST /rest/v1/rpc/set_pet_location. */
@Serializable
data class SetLocationRequest(
    @SerialName("p_pet_id") val petId: String,
    val lat: Double,
    val lng: Double,
)

@Serializable
data class PetPhotoInsert(
    @SerialName("pet_id") val petId: String,
    @SerialName("storage_path") val storagePath: String,
    @SerialName("is_primary") val isPrimary: Boolean = true,
)
