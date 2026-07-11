package com.pettrack.app.core.session

import kotlinx.coroutines.flow.StateFlow

/**
 * Abstraction over the auth session so repositories / the OkHttp authenticator can
 * be unit-tested on the JVM without an Android context (the concrete
 * [SessionManager] uses EncryptedSharedPreferences).
 */
interface SessionStore {
    val authState: StateFlow<AuthState>
    val accessToken: String?
    val refreshToken: String?
    val userId: String?
    val email: String?
    fun saveSession(accessToken: String, refreshToken: String, userId: String, email: String?)
    fun updateTokens(accessToken: String, refreshToken: String)
    fun clear()
}
