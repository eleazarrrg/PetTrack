package com.pettrack.app.data.remote.api

import com.pettrack.app.data.remote.dto.ProfileDto
import com.pettrack.app.data.remote.dto.ProfileUpdate
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.Query

interface ProfileApi {

    /** GET /rest/v1/profiles?id=eq.{uid}&select=* — RLS returns only the caller's row. */
    @GET("rest/v1/profiles")
    suspend fun getProfile(
        @Query("id") idEq: String,
        @Query("select") select: String = "*",
    ): List<ProfileDto>

    /** PATCH /rest/v1/profiles?id=eq.{uid} — persists PII collected at registration. */
    @Headers("Prefer: return=representation")
    @PATCH("rest/v1/profiles")
    suspend fun updateProfile(
        @Query("id") idEq: String,
        @Body body: ProfileUpdate,
    ): List<ProfileDto>
}
