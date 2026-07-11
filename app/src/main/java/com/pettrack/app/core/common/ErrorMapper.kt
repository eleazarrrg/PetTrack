package com.pettrack.app.core.common

import retrofit2.HttpException
import java.io.IOException

/** Maps exceptions to short, user-facing Spanish messages. */
fun authErrorMessage(t: Throwable): String = when (t) {
    is HttpException -> when (t.code()) {
        400, 401, 403 -> "Credenciales inválidas o correo no confirmado."
        422 -> "El correo ya está registrado o los datos no son válidos."
        in 500..599 -> "Error del servidor (${t.code()}). Intenta más tarde."
        else -> "No se pudo completar la solicitud (${t.code()})."
    }
    is IOException -> "Sin conexión. Revisa tu internet."
    else -> t.message ?: "Ocurrió un error inesperado."
}
