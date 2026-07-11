package com.pettrack.app.ui.notifications

import com.pettrack.app.core.notifications.NotificationCenter
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
class NotificationsViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val api = FakeNotificationApi()
    private val session = FakeSessionStore().apply { userId = "uid-1" }

    private fun viewModel(): NotificationsViewModel {
        val repo = NotificationsRepository(api, session, mainRule.dispatcher)
        val center = NotificationCenter(repo, FakeNotifier())
        return NotificationsViewModel(repo, center)
    }

    @Test
    fun load_populatesItems() = runTest {
        api.items = listOf(
            NotificationDto(id = "n1", userId = "uid-1", title = "Avistamiento de Rocky", body = "En el parque", read = false),
        )
        val vm = viewModel()
        assertEquals(1, vm.state.value.items.size)
        assertEquals("Avistamiento de Rocky", vm.state.value.items[0].title)
    }

    @Test
    fun markAllRead_callsApi() = runTest {
        val vm = viewModel()
        vm.markAllRead()
        assertEquals(1, api.markAllReadCalls)
    }
}
