package com.pettrack.app.fakes

import com.pettrack.app.data.remote.api.NotificationApi
import com.pettrack.app.data.remote.dto.MarkReadBody
import com.pettrack.app.data.remote.dto.NotificationDto
import retrofit2.Response

class FakeNotificationApi : NotificationApi {
    var items: List<NotificationDto> = emptyList()
    var markReadCalls = 0
    var markAllReadCalls = 0

    override suspend fun list(userIdEq: String, select: String, order: String): List<NotificationDto> = items

    override suspend fun markRead(idEq: String, body: MarkReadBody): Response<Unit> {
        markReadCalls++
        return Response.success(Unit)
    }

    override suspend fun markAllRead(userIdEq: String, readEq: String, body: MarkReadBody): Response<Unit> {
        markAllReadCalls++
        return Response.success(Unit)
    }
}
