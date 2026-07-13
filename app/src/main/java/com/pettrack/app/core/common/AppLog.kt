package com.pettrack.app.core.common

import android.util.Log
import com.pettrack.app.BuildConfig

/**
 * Minimal centralized logging. The app previously had NO logging, so every swallowed error
 * (`runCatching`, `catch { null }`, `getOrDefault`) destroyed its cause with no trace. Use this
 * at those points: debug builds log freely; release keeps warn/error (useful for diagnosis) but
 * drops debug chatter.
 */
object AppLog {
    private const val TAG = "PetTrack"

    fun d(message: String) {
        if (BuildConfig.DEBUG) Log.d(TAG, message)
    }

    fun w(message: String, t: Throwable? = null) {
        if (t != null) Log.w(TAG, message, t) else Log.w(TAG, message)
    }

    fun e(message: String, t: Throwable? = null) {
        if (t != null) Log.e(TAG, message, t) else Log.e(TAG, message)
    }
}
