package com.pettrack.app.data.repository

import com.pettrack.app.core.di.IoDispatcher
import com.pettrack.app.core.session.SessionStore
import com.pettrack.app.data.remote.api.NotificationApi
import com.pettrack.app.data.remote.dto.NotificationDto
import com.pettrack.app.domain.model.AppNotification
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsRepository @Inject constructor(
    private val api: NotificationApi,
    private val session: SessionStore,
    @IoDispatcher private val io: CoroutineDispatcher,
) {
    suspend fun list(): Result<List<AppNotification>> = runCatching {
        withContext(io) {
            val uid = session.userId ?: error("Sesión no iniciada")
            api.list("eq.$uid").map { it.toDomain() }
        }
    }

    suspend fun markRead(id: String): Result<Unit> = runCatching {
        withContext(io) { api.markRead("eq.$id"); Unit }
    }

    suspend fun markAllRead(): Result<Unit> = runCatching {
        withContext(io) {
            val uid = session.userId ?: error("Sesión no iniciada")
            api.markAllRead("eq.$uid")
            Unit
        }
    }
}

private fun NotificationDto.toDomain() = AppNotification(
    id = id,
    title = title,
    body = body,
    read = read,
    createdAt = createdAt,
)
