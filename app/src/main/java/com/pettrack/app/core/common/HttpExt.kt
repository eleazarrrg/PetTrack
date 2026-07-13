package com.pettrack.app.core.common

import retrofit2.HttpException
import retrofit2.Response

/**
 * Retrofit does NOT throw on a non-2xx response when the method returns `Response<T>` — it hands
 * back the response and it's the caller's job to check. Call this on fire-and-forget writes so a
 * server rejection (RLS, 4xx, 5xx) becomes a real failure inside `runCatching` — mapped to a
 * user-facing message by [authErrorMessage] — instead of being silently treated as success.
 */
fun Response<*>.throwIfFailed() {
    if (!isSuccessful) throw HttpException(this)
}
