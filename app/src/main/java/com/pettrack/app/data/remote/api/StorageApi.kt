package com.pettrack.app.data.remote.api

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface StorageApi {

    /** Upload raw image bytes to the public `pet-photos` bucket at {owner}/{pet}/{file}. */
    @POST("storage/v1/object/pet-photos/{path}")
    suspend fun upload(
        @Path("path", encoded = true) path: String,
        @Body body: RequestBody,
        @Header("Content-Type") contentType: String,
        @Header("x-upsert") upsert: String = "true",
    ): Response<Unit>
}
