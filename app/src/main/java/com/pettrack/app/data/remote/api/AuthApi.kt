package com.pettrack.app.data.remote.api

import com.pettrack.app.data.remote.dto.SignInRequest
import com.pettrack.app.data.remote.dto.SignUpRequest
import com.pettrack.app.data.remote.dto.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {

    /** Register. Returns a Response so an existing-user error doesn't throw; we sign in next. */
    @POST("auth/v1/signup")
    suspend fun signUp(@Body body: SignUpRequest): Response<TokenResponse>

    /** Password grant → access + refresh tokens. Throws HttpException on bad credentials. */
    @POST("auth/v1/token")
    suspend fun signInWithPassword(
        @Query("grant_type") grantType: String = "password",
        @Body body: SignInRequest,
    ): TokenResponse

    @POST("auth/v1/logout")
    suspend fun logout(): Response<Unit>
}
