package com.pettrack.app.ui.auth.register

import com.pettrack.app.data.repository.AuthRepository
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
class RegisterViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val authApi = FakeAuthApi()
    private val profileApi = FakeProfileApi()
    private val session = FakeSessionStore()

    private fun viewModel() =
        RegisterViewModel(AuthRepository(authApi, profileApi, session, mainRule.dispatcher))

    @Test
    fun blankName_setsError() = runTest {
        val vm = viewModel()
        vm.onEmail("a@b.com")
        vm.onPassword("pw1234")
        vm.submit()
        assertEquals("Ingresa tu nombre completo.", vm.state.value.error)
    }

    @Test
    fun shortPassword_setsError() = runTest {
        val vm = viewModel()
        vm.onFullName("Ana")
        vm.onEmail("a@b.com")
        vm.onPassword("123")
        vm.submit()
        assertEquals("La contraseña debe tener al menos 6 caracteres.", vm.state.value.error)
    }

    @Test
    fun validForm_registersAndPersistsProfile() = runTest {
        val vm = viewModel()
        vm.onFullName("Ana")
        vm.onNationalId("8-1")
        vm.onPhone("6000")
        vm.onEmail("a@b.com")
        vm.onAddress("Calle")
        vm.onPassword("pw1234")
        vm.submit()
        assertTrue(vm.state.value.success)
        assertEquals(1, authApi.signUpCalls)
        assertEquals(1, authApi.signInCalls)
        assertEquals(1, profileApi.updateCalls)
        assertEquals("Ana", profileApi.lastUpdate?.fullName)
    }
}
