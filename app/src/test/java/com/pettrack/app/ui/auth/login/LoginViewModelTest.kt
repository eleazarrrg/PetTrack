package com.pettrack.app.ui.auth.login

import com.pettrack.app.data.repository.AuthRepository
import com.pettrack.app.fakes.FakeAuthApi
import com.pettrack.app.fakes.FakeProfileApi
import com.pettrack.app.fakes.FakeSessionStore
import com.pettrack.app.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val authApi = FakeAuthApi()
    private val session = FakeSessionStore()

    private fun viewModel() =
        LoginViewModel(AuthRepository(authApi, FakeProfileApi(), session, mainRule.dispatcher))

    @Test
    fun blankInput_setsValidationError() = runTest {
        val vm = viewModel()
        vm.submit()
        assertEquals("Ingresa tu correo y contraseña.", vm.state.value.error)
        assertFalse(vm.state.value.success)
    }

    @Test
    fun validCredentials_setSuccess() = runTest {
        val vm = viewModel()
        vm.onEmailChange("a@b.com")
        vm.onPasswordChange("pw")
        vm.submit()
        assertTrue(vm.state.value.success)
        assertFalse(vm.state.value.loading)
        assertNull(vm.state.value.error)
        assertEquals("acc", session.accessToken)
    }

    @Test
    fun badCredentials_setMappedError() = runTest {
        authApi.signInError = HttpException(
            Response.error<Any>(400, """{"error_code":"invalid_credentials"}""".toResponseBody(null)),
        )
        val vm = viewModel()
        vm.onEmailChange("a@b.com")
        vm.onPasswordChange("bad")
        vm.submit()
        assertFalse(vm.state.value.success)
        assertTrue(vm.state.value.error!!.contains("incorrect", ignoreCase = true))
    }
}
