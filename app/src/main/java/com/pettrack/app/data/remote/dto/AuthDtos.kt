package com.pettrack.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val email: String,
    val password: String,
    val data: SignUpData? = null,
)

@Serializable
data class SignUpData(
    @SerialName("full_name") val fullName: String,
)

@Serializable
data class SignInRequest(
    val email: String,
    val password: String,
)

@Serializable
data class TokenResponse(
    @SerialName("access_token") val accessToken: String? = null,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("token_type") val tokenType: String? = null,
    @SerialName("expires_in") val expiresIn: Long? = null,
    val user: AuthUserDto? = null,
)

@Serializable
data class AuthUserDto(
    val id: String,
    val email: String? = null,
)
