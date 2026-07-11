package com.pettrack.app.data.remote.api

import com.pettrack.app.data.remote.dto.MarkReadBody
import com.pettrack.app.data.remote.dto.NotificationDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface NotificationApi {

    @GET("rest/v1/notifications")
    suspend fun list(
        @Query("user_id") userIdEq: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc",
    ): List<NotificationDto>

    @PATCH("rest/v1/notifications")
    suspend fun markRead(
        @Query("id") idEq: String,
        @Body body: MarkReadBody = MarkReadBody(),
    ): Response<Unit>

    @PATCH("rest/v1/notifications")
    suspend fun markAllRead(
        @Query("user_id") userIdEq: String,
        @Query("read") readEq: String = "eq.false",
        @Body body: MarkReadBody = MarkReadBody(),
    ): Response<Unit>
}
