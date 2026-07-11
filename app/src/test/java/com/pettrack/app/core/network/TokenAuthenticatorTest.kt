package com.pettrack.app.core.network

import com.pettrack.app.fakes.FakeSessionStore
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TokenAuthenticatorTest {

    private lateinit var server: MockWebServer
    private lateinit var session: FakeSessionStore
    private lateinit var authenticator: TokenAuthenticator
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
        session = FakeSessionStore().apply {
            accessToken = "old"
            refreshToken = "ref"
        }
        val baseUrl = server.url("").toString().removeSuffix("/")
        authenticator = TokenAuthenticator(session, json, baseUrl, OkHttpClient())
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    private fun unauthorizedResponse(): Response {
        val request = Request.Builder()
            .url(server.url("/rest/v1/pets"))
            .header("Authorization", "Bearer old")
            .build()
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .build()
    }

    @Test
    fun on401_refreshesTokenAndRetriesWithNewBearer() {
        server.enqueue(
            MockResponse().setResponseCode(200)
                .setBody("""{"access_token":"newacc","refresh_token":"newref"}"""),
        )
        val retried = authenticator.authenticate(null, unauthorizedResponse())
        assertNotNull(retried)
        assertEquals("Bearer newacc", retried!!.header("Authorization"))
        assertEquals("newacc", session.accessToken)
        assertEquals("newref", session.refreshToken)
    }

    @Test
    fun refreshFailure_clearsSessionAndGivesUp() {
        server.enqueue(MockResponse().setResponseCode(401).setBody("{}"))
        val retried = authenticator.authenticate(null, unauthorizedResponse())
        assertNull(retried)
        assertTrue(session.cleared)
    }

    @Test
    fun noRefreshToken_givesUpImmediately() {
        session.refreshToken = null
        val retried = authenticator.authenticate(null, unauthorizedResponse())
        assertNull(retried)
    }
}
