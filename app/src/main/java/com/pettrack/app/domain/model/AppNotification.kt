package com.pettrack.app.domain.model

data class AppNotification(
    val id: String,
    val title: String,
    val body: String?,
    val read: Boolean,
    val createdAt: String?,
)
