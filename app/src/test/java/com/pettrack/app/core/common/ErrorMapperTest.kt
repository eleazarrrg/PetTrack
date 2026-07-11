package com.pettrack.app.core.common

import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ErrorMapperTest {

    private fun http(code: Int) = HttpException(Response.error<Any>(code, "".toResponseBody(null)))

    @Test
    fun unauthorized_mapsToCredentials() {
        assertEquals("Credenciales inválidas o correo no confirmado.", authErrorMessage(http(401)))
    }

    @Test
    fun conflict422_mapsToAlreadyRegistered() {
        assertEquals("El correo ya está registrado o los datos no son válidos.", authErrorMessage(http(422)))
    }

    @Test
    fun serverError_mentionsServer() {
        assertTrue(authErrorMessage(http(500)).contains("servidor"))
    }

    @Test
    fun ioException_mentionsConnection() {
        assertTrue(authErrorMessage(IOException("boom")).contains("conexión"))
    }
}
