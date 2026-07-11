package com.pettrack.app.data.repository

import com.pettrack.app.core.di.IoDispatcher
import com.pettrack.app.core.session.SessionStore
import com.pettrack.app.data.remote.api.ProfileApi
import com.pettrack.app.data.remote.dto.ProfileDto
import com.pettrack.app.data.remote.dto.ProfileUpdate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val profileApi: ProfileApi,
    private val session: SessionStore,
    @IoDispatcher private val io: CoroutineDispatcher,
) {
    val email: String? get() = session.email

    suspend fun myProfile(): Result<ProfileDto> = runCatching {
        withContext(io) {
            val uid = session.userId ?: error("Sesión no iniciada")
            profileApi.getProfile("eq.$uid").firstOrNull() ?: error("Perfil no encontrado")
        }
    }

    suspend fun update(fullName: String, nationalId: String, phone: String, address: String): Result<Unit> =
        runCatching {
            withContext(io) {
                val uid = session.userId ?: error("Sesión no iniciada")
                profileApi.updateProfile(
                    idEq = "eq.$uid",
                    body = ProfileUpdate(
                        fullName = fullName.trim().ifBlank { null },
                        nationalId = nationalId.trim().ifBlank { null },
                        phone = phone.trim().ifBlank { null },
                        address = address.trim().ifBlank { null },
                    ),
                )
                Unit
            }
        }
}
