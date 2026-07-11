package com.pettrack.app.core.notifications

import com.pettrack.app.data.repository.NotificationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * App-wide holder for the unread badge count. On each [refresh] it fetches the user's
 * notifications, updates the badge, and fires a system notification for genuinely new
 * unread items (the first refresh only seeds the baseline, so old items don't spam).
 */
@Singleton
class NotificationCenter @Inject constructor(
    private val repository: NotificationsRepository,
    private val notifier: Notifier,
) {
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private val mutex = Mutex()
    private var seeded = false
    private var seenIds = emptySet<String>()

    suspend fun refresh() {
        val list = repository.list().getOrNull() ?: return
        mutex.withLock {
            _unreadCount.value = list.count { !it.read }
            if (!seeded) {
                seenIds = list.map { it.id }.toSet()
                seeded = true
                return
            }
            list.filter { !it.read && it.id !in seenIds }
                .forEach { notifier.notify(it.id.hashCode(), it.title, it.body ?: "Nuevo avistamiento") }
            seenIds = list.map { it.id }.toSet()
        }
    }

    fun reset() {
        seeded = false
        seenIds = emptySet()
        _unreadCount.value = 0
    }
}
