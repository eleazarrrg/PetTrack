package com.pettrack.app.ui.profile

import com.pettrack.app.data.repository.AuthRepository
import com.pettrack.app.data.repository.ProfileRepository
import com.pettrack.app.data.remote.dto.ProfileDto
import com.pettrack.app.fakes.FakeAuthApi
import com.pettrack.app.fakes.FakeProfileApi
import com.pettrack.app.fakes.FakeSessionStore
import com.pettrack.app.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val profileApi = FakeProfileApi()
    private val session = FakeSessionStore().apply { userId = "uid-1"; email = "a@b.com" }

    private fun viewModel(): ProfileViewModel {
        val profileRepo = ProfileRepository(profileApi, session, mainRule.dispatcher)
        val authRepo = AuthRepository(FakeAuthApi(), profileApi, session, mainRule.dispatcher)
        return ProfileViewModel(profileRepo, authRepo)
    }

    @Test
    fun load_populatesFieldsFromProfile() = runTest {
        profileApi.profile = ProfileDto(
            id = "uid-1", fullName = "Ana", nationalId = "8-1",
            phone = "6000", email = "a@b.com", address = "Calle",
        )
        val vm = viewModel()
        assertEquals("Ana", vm.state.value.fullName)
        assertEquals("8-1", vm.state.value.nationalId)
        assertEquals("6000", vm.state.value.phone)
    }

    @Test
    fun save_callsUpdateAndSetsSaved() = runTest {
        val vm = viewModel()
        vm.onFullName("Nuevo Nombre")
        vm.save()
        assertEquals(1, profileApi.updateCalls)
        assertEquals("Nuevo Nombre", profileApi.lastUpdate?.fullName)
        assertTrue(vm.state.value.saved)
    }

    @Test
    fun logout_setsLoggedOut() = runTest {
        val vm = viewModel()
        vm.logout()
        assertTrue(vm.state.value.loggedOut)
    }
}
