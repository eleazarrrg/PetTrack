package com.pettrack.app.data.repository

import com.pettrack.app.core.di.IoDispatcher
import com.pettrack.app.core.session.SessionStore
import com.pettrack.app.data.remote.api.AuthApi
import com.pettrack.app.data.remote.api.ProfileApi
import com.pettrack.app.data.remote.dto.ProfileUpdate
import com.pettrack.app.data.remote.dto.SignInRequest
import com.pettrack.app.data.remote.dto.SignUpData
import com.pettrack.app.data.remote.dto.SignUpRequest
import com.pettrack.app.data.remote.dto.TokenResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val profileApi: ProfileApi,
    private val session: SessionStore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        withContext(ioDispatcher) {
            val tokens = authApi.signInWithPassword(body = SignInRequest(email.trim(), password))
            persist(tokens)
        }
    }

    /**
     * Registers, signs in to get a guaranteed session, then persists the remaining
     * owner PII (cédula, phone, address) to the profile row the signup trigger created.
     * NOTE: "Confirm email" must be disabled in Supabase Auth for immediate sign-in.
     */
    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        nationalId: String,
        phone: String,
        address: String,
    ): Result<Unit> = runCatching {
        withContext(ioDispatcher) {
            authApi.signUp(SignUpRequest(email.trim(), password, SignUpData(fullName.trim())))
            val tokens = authApi.signInWithPassword(body = SignInRequest(email.trim(), password))
            persist(tokens)
            val uid = session.userId ?: error("No user id after sign-in")
            profileApi.updateProfile(
                idEq = "eq.$uid",
                body = ProfileUpdate(
                    fullName = fullName.trim().ifBlank { null },
                    nationalId = nationalId.trim().ifBlank { null },
                    phone = phone.trim().ifBlank { null },
                    address = address.trim().ifBlank { null },
                ),
            )
            Unit
        }
    }

    suspend fun logout() {
        withContext(ioDispatcher) {
            runCatching { authApi.logout() }
            session.clear()
        }
    }

    private fun persist(tokens: TokenResponse) {
        val access = tokens.accessToken ?: error("No access token returned")
        val refresh = tokens.refreshToken ?: error("No refresh token returned")
        val user = tokens.user ?: error("No user returned")
        session.saveSession(access, refresh, user.id, user.email)
    }
}
