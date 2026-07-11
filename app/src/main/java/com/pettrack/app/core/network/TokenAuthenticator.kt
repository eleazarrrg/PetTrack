package com.pettrack.app.core.network

import com.pettrack.app.BuildConfig
import com.pettrack.app.core.session.SessionStore
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * On a 401, exchanges the stored refresh token for a fresh access token via
 * `POST {baseUrl}/auth/v1/token?grant_type=refresh_token`, then retries the original
 * request. If the refresh fails, the session is cleared (→ Unauthenticated).
 *
 * baseUrl + bareClient are injected so this is unit-testable against a MockWebServer.
 * The bare client has no HeaderInterceptor/Authenticator so the refresh cannot recurse.
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val session: SessionStore,
    private val json: Json,
    @Named("supabaseUrl") private val baseUrl: String,
    @Named("bareClient") private val bareClient: OkHttpClient,
) : Authenticator {

    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null // already retried once
        val refreshToken = session.refreshToken ?: return null

        val newAccess: String = synchronized(lock) {
            val tokenUsed = response.request.header("Authorization")?.removePrefix("Bearer ")
            val current = session.accessToken
            // Another thread may have already refreshed while we were blocked.
            if (current != null && current != tokenUsed) {
                current
            } else {
                refreshBlocking(refreshToken) ?: run {
                    session.clear()
                    return null
                }
            }
        }

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccess")
            .build()
    }

    private fun refreshBlocking(refreshToken: String): String? {
        return try {
            val url = "$baseUrl/auth/v1/token?grant_type=refresh_token"
            val body = json.encodeToString(RefreshRequest.serializer(), RefreshRequest(refreshToken))
                .toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()
            bareClient.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) return null
                val payload = resp.body?.string() ?: return null
                val tokens = json.decodeFromString(RefreshResponse.serializer(), payload)
                session.updateTokens(tokens.accessToken, tokens.refreshToken)
                tokens.accessToken
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    @Serializable
    private data class RefreshRequest(val refresh_token: String)

    @Serializable
    private data class RefreshResponse(
        @kotlinx.serialization.SerialName("access_token") val accessToken: String,
        @kotlinx.serialization.SerialName("refresh_token") val refreshToken: String,
    )
}
