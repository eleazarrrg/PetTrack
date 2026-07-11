package com.pettrack.app.core.notifications

import com.pettrack.app.data.remote.dto.NotificationDto
import com.pettrack.app.data.repository.NotificationsRepository
import com.pettrack.app.fakes.FakeNotificationApi
import com.pettrack.app.fakes.FakeNotifier
import com.pettrack.app.fakes.FakeSessionStore
import com.pettrack.app.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationCenterTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val api = FakeNotificationApi()
    private val session = FakeSessionStore().apply { userId = "uid-1" }
    private val notifier = FakeNotifier()

    private fun center(): NotificationCenter {
        val repo = NotificationsRepository(api, session, mainRule.dispatcher)
        return NotificationCenter(repo, notifier)
    }

    private fun notif(id: String, read: Boolean) =
        NotificationDto(id = id, userId = "uid-1", title = "Avistamiento", body = "Nota", read = read)

    @Test
    fun firstRefresh_seedsBaseline_setsUnread_doesNotFire() = runTest {
        api.items = listOf(notif("n1", read = false))
        val c = center()
        c.refresh()
        assertEquals(1, c.unreadCount.value)
        assertEquals(0, notifier.notified.size) // baseline seed, no system notification
    }

    @Test
    fun secondRefresh_firesForNewUnreadOnly() = runTest {
        val c = center()
        api.items = listOf(notif("n1", read = false))
        c.refresh() // seed
        api.items = listOf(notif("n1", read = false), notif("n2", read = false))
        c.refresh() // new n2 -> fire once
        assertEquals(1, notifier.notified.size)
        assertEquals(2, c.unreadCount.value)
    }
}
