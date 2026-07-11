package com.pettrack.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("national_id") val nationalId: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
)

/** Partial update body for PATCH /rest/v1/profiles. Null fields are omitted (explicitNulls=false). */
@Serializable
data class ProfileUpdate(
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("national_id") val nationalId: String? = null,
    val phone: String? = null,
    val address: String? = null,
)
