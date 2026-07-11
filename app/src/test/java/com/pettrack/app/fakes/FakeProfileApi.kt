package com.pettrack.app.fakes

import com.pettrack.app.data.remote.api.ProfileApi
import com.pettrack.app.data.remote.dto.ProfileDto
import com.pettrack.app.data.remote.dto.ProfileUpdate

class FakeProfileApi : ProfileApi {
    var updateCalls = 0
    var lastUpdate: ProfileUpdate? = null
    var lastIdEq: String? = null
    var profile: ProfileDto? = null

    override suspend fun getProfile(idEq: String, select: String): List<ProfileDto> = listOfNotNull(profile)

    override suspend fun updateProfile(idEq: String, body: ProfileUpdate): List<ProfileDto> {
        updateCalls++
        lastUpdate = body
        lastIdEq = idEq
        return emptyList()
    }
}
