package com.pettrack.app.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pettrack.app.core.common.authErrorMessage
import com.pettrack.app.core.notifications.NotificationCenter
import com.pettrack.app.data.repository.NotificationsRepository
import com.pettrack.app.domain.model.AppNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUiState(
    val loading: Boolean = true,
    val items: List<AppNotification> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: NotificationsRepository,
    private val center: NotificationCenter,
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsUiState())
    val state: StateFlow<NotificationsUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            repository.list()
                .onSuccess { items -> _state.update { it.copy(loading = false, items = items) } }
                .onFailure { e -> _state.update { it.copy(loading = false, error = authErrorMessage(e)) } }
            center.refresh()
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            repository.markAllRead()
                .onSuccess { load() }
                .onFailure { e -> _state.update { it.copy(error = authErrorMessage(e)) } }
        }
    }

    fun onOpen(id: String) {
        viewModelScope.launch {
            repository.markRead(id)
                .onSuccess { load() }
                .onFailure { e -> _state.update { it.copy(error = authErrorMessage(e)) } }
        }
    }
}
