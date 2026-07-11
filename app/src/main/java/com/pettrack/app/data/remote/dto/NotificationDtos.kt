package com.pettrack.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("pet_id") val petId: String? = null,
    val title: String,
    val body: String? = null,
    val read: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class MarkReadBody(val read: Boolean = true)
