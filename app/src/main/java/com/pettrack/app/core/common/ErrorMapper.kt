package com.pettrack.app.core.common

import com.pettrack.app.BuildConfig
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
    } catch (t: Exception) {
        AppLog.w("Could not read error body for HTTP ${e.code()}", t)
        ""
    }
    // Preserve the real cause for diagnosis. The raw body may echo submitted values (PII), so keep
    // only the status code at WARN (release-safe) and log the body at DEBUG (stripped in release).
    AppLog.w("HTTP ${e.code()} error")
    AppLog.d("HTTP ${e.code()} body: ${body.take(300)}")
    val text = body.lowercase()
    return when {
        // Clave inválida/ausente. El detalle técnico solo en debug — en release no filtramos infra.
        text.contains("no api key") || text.contains("invalid api key") ->
            if (BuildConfig.DEBUG) {
                "Problema con la clave de Supabase. Revisa la configuración (local.properties) y recompila."
            } else {
                "No se pudo conectar con el servidor. Inténtalo más tarde."
            }

        // Correo no confirmado (cuenta vieja creada cuando la confirmación estaba activa)
        text.contains("email_not_confirmed") || text.contains("email not confirmed") ->
            "Ese correo no está confirmado. Regístrate con un correo nuevo, o confírmalo desde el enlace que te llegó."

        // El proveedor de correo o los registros están apagados en el servidor
        text.contains("email_provider_disabled") || text.contains("logins are disabled") ||
            text.contains("signups not allowed") || text.contains("signup is disabled") ||
            text.contains("email logins are disabled") ->
            "El registro/inicio por correo está deshabilitado en el servidor."

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

        // 403 = política del servidor (RLS): permiso, no credenciales.
        e.code() == 403 -> "No tienes permiso para realizar esta acción."

        e.code() == 401 -> "No autorizado. Revisa tus credenciales e inténtalo de nuevo."

        e.code() in 500..599 -> "Error del servidor (${e.code()}). Intenta más tarde."
        else -> "No se pudo completar la solicitud (${e.code()})."
    }
}
