package com.pettrack.app.fakes

import com.pettrack.app.data.remote.api.StorageApi
import okhttp3.RequestBody
import retrofit2.Response

class FakeStorageApi : StorageApi {
    override suspend fun upload(
        path: String,
        body: RequestBody,
        contentType: String,
        upsert: String,
    ): Response<Unit> = Response.success(Unit)
}
