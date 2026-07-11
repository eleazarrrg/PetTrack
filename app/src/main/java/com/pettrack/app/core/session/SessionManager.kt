package com.pettrack.app.core.session

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for the auth session. Tokens are stored in
 * EncryptedSharedPreferences; [authState] drives navigation. Reads are synchronous
 * so the OkHttp interceptor/authenticator can access tokens on the network thread.
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context,
) : SessionStore {
    private val prefs: SharedPreferences = run {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "pettrack_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    private val _authState = MutableStateFlow(readInitialState())
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    override val accessToken: String? get() = prefs.getString(KEY_ACCESS, null)
    override val refreshToken: String? get() = prefs.getString(KEY_REFRESH, null)
    override val userId: String? get() = prefs.getString(KEY_UID, null)
    override val email: String? get() = prefs.getString(KEY_EMAIL, null)

    override fun saveSession(accessToken: String, refreshToken: String, userId: String, email: String?) {
        prefs.edit()
            .putString(KEY_ACCESS, accessToken)
            .putString(KEY_REFRESH, refreshToken)
            .putString(KEY_UID, userId)
            .putString(KEY_EMAIL, email)
            .apply()
        _authState.value = AuthState.Authenticated(userId, email)
    }

    override fun updateTokens(accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString(KEY_ACCESS, accessToken)
            .putString(KEY_REFRESH, refreshToken)
            .apply()
    }

    override fun clear() {
        prefs.edit().clear().apply()
        _authState.value = AuthState.Unauthenticated
    }

    private fun readInitialState(): AuthState {
        val uid = prefs.getString(KEY_UID, null)
        val access = prefs.getString(KEY_ACCESS, null)
        return if (uid != null && access != null) {
            AuthState.Authenticated(uid, prefs.getString(KEY_EMAIL, null))
        } else {
            AuthState.Unauthenticated
        }
    }

    private companion object {
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
        const val KEY_UID = "user_id"
        const val KEY_EMAIL = "email"
    }
}
