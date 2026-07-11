package com.pettrack.app.fakes

import com.pettrack.app.core.session.AuthState
import com.pettrack.app.core.session.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeSessionStore : SessionStore {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    override val authState: StateFlow<AuthState> = _authState

    override var accessToken: String? = null
    override var refreshToken: String? = null
    override var userId: String? = null
    override var email: String? = null
    var cleared = false

    override fun saveSession(accessToken: String, refreshToken: String, userId: String, email: String?) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.userId = userId
        this.email = email
        _authState.value = AuthState.Authenticated(userId, email)
    }

    override fun updateTokens(accessToken: String, refreshToken: String) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    override fun clear() {
        accessToken = null
        refreshToken = null
        userId = null
        email = null
        cleared = true
        _authState.value = AuthState.Unauthenticated
    }
}
