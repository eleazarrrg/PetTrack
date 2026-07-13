package com.pettrack.app.core.common

import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class HttpExtTest {

    @Test
    fun throwIfFailed_throwsHttpException_onNon2xx() {
        val response = Response.error<Unit>(403, "forbidden".toResponseBody(null))
        var thrown = false
        try {
            response.throwIfFailed()
        } catch (e: HttpException) {
            thrown = true
            assertEquals(403, e.code())
        }
        assertTrue("Expected HttpException on a 403 response", thrown)
    }

    @Test
    fun throwIfFailed_doesNothing_on2xx() {
        try {
            Response.success(Unit).throwIfFailed()
        } catch (e: Exception) {
            fail("Should not throw on a successful response: $e")
        }
    }
}
