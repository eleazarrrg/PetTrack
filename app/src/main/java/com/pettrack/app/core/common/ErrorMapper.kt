package com.pettrack.app.core.common

import retrofit2.HttpException
import java.io.IOException

/**
 * Maps exceptions to short, user-facing Spanish messages. For HTTP errors it inspects
 * the server's error body so the message points at the REAL cause (config faltante,
 * correo no confirmado, proveedor deshabilitado, credenciales, etc.).
 */
fun authErrorMessage(t: Throwable): String = when (t) {
    is HttpException -> mapHttpError(t)
    is IOException -> "Sin conexión. Revisa tu internet."
    else -> t.message ?: "Ocurrió un error inesperado."
}

private fun mapHttpError(e: HttpException): String {
    val body = try {
        e.response()?.errorBody()?.string().orEmpty()
    } catch (_: Exception) {
        ""
    }
    val text = body.lowercase()
    return when {
        // Falta o es inválida la clave (local.properties no configurado / build sin la clave)
        text.contains("no api key") || text.contains("invalid api key") ||
            text.contains("apikey") ->
            "Falta configurar la clave de Supabase. Crea el archivo local.properties (ver README) y vuelve a compilar."

        // Correo no confirmado (cuenta vieja creada cuando la confirmación estaba activa)
        text.contains("email_not_confirmed") || text.contains("email not confirmed") ->
            "Ese correo no está confirmado. Regístrate con un correo nuevo, o confírmalo desde el enlace que te llegó."

        // El proveedor de correo o los registros están apagados en Supabase
        text.contains("email_provider_disabled") || text.contains("logins are disabled") ||
            text.contains("signups not allowed") || text.contains("signup is disabled") ||
            text.contains("email logins are disabled") ->
            "El registro/inicio por correo está deshabilitado en el servidor. Actívalo en Supabase (Authentication → Email)."

        // El correo ya existe
        text.contains("user_already_exists") || text.contains("already registered") ||
            text.contains("email_exists") ->
            "Ese correo ya está registrado. Inicia sesión en su lugar."

        // Credenciales incorrectas
        text.contains("invalid_credentials") || text.contains("invalid_grant") ||
            text.contains("invalid login") ->
            "Correo o contraseña incorrectos."

        // Contraseña débil
        text.contains("weak_password") || text.contains("password should be") ->
            "La contraseña es muy débil (usa al menos 6 caracteres)."

        e.code() == 401 || e.code() == 403 ->
            "No autorizado. Revisa la configuración de la app (local.properties) o tus credenciales."

        e.code() in 500..599 -> "Error del servidor (${e.code()}). Intenta más tarde."
        else -> "No se pudo completar la solicitud (${e.code()})."
    }
}
