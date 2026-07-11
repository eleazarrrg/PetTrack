package com.pettrack.app.data.repository

import com.pettrack.app.fakes.FakeAuthApi
import com.pettrack.app.fakes.FakeProfileApi
import com.pettrack.app.fakes.FakeSessionStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryTest {

    private lateinit var authApi: FakeAuthApi
    private lateinit var profileApi: FakeProfileApi
    private lateinit var session: FakeSessionStore
    private lateinit var repo: AuthRepository

    @Before
    fun setup() {
        authApi = FakeAuthApi()
        profileApi = FakeProfileApi()
        session = FakeSessionStore()
        repo = AuthRepository(authApi, profileApi, session, UnconfinedTestDispatcher())
    }

    @Test
    fun login_success_persistsSession() = runTest {
        val result = repo.login("a@b.com", "pw")
        assertTrue(result.isSuccess)
        assertEquals("acc", session.accessToken)
        assertEquals("ref", session.refreshToken)
        assertEquals("uid-1", session.userId)
    }

    @Test
    fun login_failure_returnsFailureAndNoSession() = runTest {
        authApi.signInError = HttpException(Response.error<Any>(400, "".toResponseBody(null)))
        val result = repo.login("a@b.com", "bad")
        assertTrue(result.isFailure)
        assertNull(session.accessToken)
    }

    @Test
    fun register_signsUp_signsIn_andPatchesProfile() = runTest {
        val result = repo.register("a@b.com", "pw123", "Ana", "8-1", "6000", "Calle")
        assertTrue(result.isSuccess)
        assertEquals(1, authApi.signUpCalls)
        assertEquals(1, authApi.signInCalls)
        assertEquals(1, profileApi.updateCalls)
        assertEquals("Ana", profileApi.lastUpdate?.fullName)
        assertEquals("8-1", profileApi.lastUpdate?.nationalId)
        assertEquals("eq.uid-1", profileApi.lastIdEq)
    }

    @Test
    fun logout_clearsSession() = runTest {
        session.saveSession("a", "b", "uid", "e@x.com")
        repo.logout()
        assertTrue(session.cleared)
        assertNull(session.accessToken)
        assertEquals(1, authApi.logoutCalls)
    }
}
