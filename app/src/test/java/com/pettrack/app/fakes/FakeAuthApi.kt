package com.pettrack.app.fakes

import com.pettrack.app.data.remote.api.AuthApi
import com.pettrack.app.data.remote.dto.AuthUserDto
import com.pettrack.app.data.remote.dto.SignInRequest
import com.pettrack.app.data.remote.dto.SignUpRequest
import com.pettrack.app.data.remote.dto.TokenResponse
import retrofit2.Response

class FakeAuthApi : AuthApi {
    var tokenResponse: TokenResponse = TokenResponse(
        accessToken = "acc",
        refreshToken = "ref",
        user = AuthUserDto(id = "uid-1", email = "a@b.com"),
    )
    var signInError: Throwable? = null
    var signUpCalls = 0
    var signInCalls = 0
    var logoutCalls = 0

    override suspend fun signUp(body: SignUpRequest): Response<TokenResponse> {
        signUpCalls++
        return Response.success(tokenResponse)
    }

    override suspend fun signInWithPassword(grantType: String, body: SignInRequest): TokenResponse {
        signInCalls++
        signInError?.let { throw it }
        return tokenResponse
    }

    override suspend fun logout(): Response<Unit> {
        logoutCalls++
        return Response.success(Unit)
    }
}
