package com.pettrack.app.ui.notifications

import androidx.lifecycle.ViewModel
import com.pettrack.app.core.notifications.NotificationCenter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Exposes the unread badge and a one-shot [refresh]. Polling is driven by the UI layer under
 * `repeatOnLifecycle` so it pauses when the app is backgrounded (no wasted network/battery),
 * instead of looping for the whole process lifetime.
 */
@HiltViewModel
class NotificationWatcherViewModel @Inject constructor(
    private val center: NotificationCenter,
) : ViewModel() {

    val unreadCount: StateFlow<Int> = center.unreadCount

    suspend fun refresh() = center.refresh()

    fun reset() = center.reset()
}
