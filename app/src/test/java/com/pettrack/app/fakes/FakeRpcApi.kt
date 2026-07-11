package com.pettrack.app.fakes

import com.pettrack.app.data.remote.api.RpcApi
import com.pettrack.app.data.remote.dto.DashboardRequest
import com.pettrack.app.data.remote.dto.DashboardStatsDto
import com.pettrack.app.data.remote.dto.OwnerContactDto
import com.pettrack.app.data.remote.dto.OwnerContactRequest
import com.pettrack.app.data.remote.dto.PetNearbyDto
import com.pettrack.app.data.remote.dto.PetsNearbyRequest
import com.pettrack.app.data.remote.dto.ReportSightingRequest
import com.pettrack.app.data.remote.dto.SetLocationRequest
import retrofit2.Response

class FakeRpcApi : RpcApi {
    var lastNearbyRequest: PetsNearbyRequest? = null
    var nearbyResult: List<PetNearbyDto> = emptyList()
    var lastSightingRequest: ReportSightingRequest? = null
    var ownerContactResult: List<OwnerContactDto> = emptyList()
    var dashboardResult: DashboardStatsDto = DashboardStatsDto()

    override suspend fun setPetLocation(body: SetLocationRequest): Response<Unit> = Response.success(Unit)

    override suspend fun petsNearby(body: PetsNearbyRequest): List<PetNearbyDto> {
        lastNearbyRequest = body
        return nearbyResult
    }

    override suspend fun reportSighting(body: ReportSightingRequest): Response<Unit> {
        lastSightingRequest = body
        return Response.success(Unit)
    }

    override suspend fun getOwnerContact(body: OwnerContactRequest): List<OwnerContactDto> = ownerContactResult

    override suspend fun dashboardStats(body: DashboardRequest): DashboardStatsDto = dashboardResult
}
