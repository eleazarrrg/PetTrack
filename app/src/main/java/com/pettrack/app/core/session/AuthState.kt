package com.pettrack.app.core.session

/** High-level authentication state derived from the stored session. */
sealed interface AuthState {
    data object Unknown : AuthState
    data class Authenticated(val userId: String, val email: String?) : AuthState
    data object Unauthenticated : AuthState
}
