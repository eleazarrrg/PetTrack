package com.pettrack.app.ui

import androidx.lifecycle.ViewModel
import com.pettrack.app.core.session.AuthState
import com.pettrack.app.core.session.SessionStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    session: SessionStore,
) : ViewModel() {
    val authState: StateFlow<AuthState> = session.authState
}
