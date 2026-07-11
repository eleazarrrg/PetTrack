package com.pettrack.app.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pettrack.app.core.notifications.NotificationCenter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Polls notifications while alive to keep the unread badge fresh and fire system notifs. */
@HiltViewModel
class NotificationWatcherViewModel @Inject constructor(
    private val center: NotificationCenter,
) : ViewModel() {

    val unreadCount: StateFlow<Int> = center.unreadCount

    init {
        viewModelScope.launch {
            while (isActive) {
                center.refresh()
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    fun reset() = center.reset()

    private companion object {
        const val POLL_INTERVAL_MS = 15_000L
    }
}
