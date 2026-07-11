package com.pettrack.app.core.network

import com.pettrack.app.BuildConfig
import com.pettrack.app.core.session.SessionStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Adds the Supabase `apikey` header to every request and an `Authorization: Bearer`
 * header using the current user access token (falling back to the anon key when
 * signed out, which PostgREST/GoTrue require).
 */
class HeaderInterceptor @Inject constructor(
    private val session: SessionStore,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val bearer = session.accessToken ?: BuildConfig.SUPABASE_ANON_KEY
        val request = chain.request().newBuilder()
            .header("apikey", BuildConfig.SUPABASE_ANON_KEY)
            .header("Authorization", "Bearer $bearer")
            .build()
        return chain.proceed(request)
    }
}
