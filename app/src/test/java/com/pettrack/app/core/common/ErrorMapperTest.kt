package com.pettrack.app.core.common

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ErrorMapperTest {

    private fun http(code: Int, body: String) =
        HttpException(Response.error<Any>(code, body.toResponseBody("application/json".toMediaType())))

    @Test
    fun missingApiKey_pointsToLocalProperties() {
        val msg = authErrorMessage(http(401, """{"message":"No API key found in request"}"""))
        assertTrue(msg.contains("local.properties"))
    }

    @Test
    fun emailNotConfirmed_isExplained() {
        val msg = authErrorMessage(http(400, """{"error_code":"email_not_confirmed","msg":"Email not confirmed"}"""))
        assertTrue(msg.contains("confirm", ignoreCase = true))
    }

    @Test
    fun emailProviderDisabled_isExplained() {
        val msg = authErrorMessage(http(422, """{"error_code":"email_provider_disabled","msg":"Email logins are disabled"}"""))
        assertTrue(msg.contains("deshabilitado"))
    }

    @Test
    fun invalidCredentials_isExplained() {
        val msg = authErrorMessage(http(400, """{"error_code":"invalid_credentials","msg":"Invalid login credentials"}"""))
        assertTrue(msg.contains("incorrect", ignoreCase = true))
    }

    @Test
    fun serverError_mentionsServer() {
        assertTrue(authErrorMessage(http(500, "")).contains("servidor"))
    }

    @Test
    fun ioException_mentionsConnection() {
        assertTrue(authErrorMessage(IOException("boom")).contains("conexión"))
    }
}
