package com.pettrack.app.data.remote.api

import com.pettrack.app.data.remote.dto.DashboardRequest
import com.pettrack.app.data.remote.dto.DashboardStatsDto
import com.pettrack.app.data.remote.dto.OwnerContactDto
import com.pettrack.app.data.remote.dto.OwnerContactRequest
import com.pettrack.app.data.remote.dto.PetNearbyDto
import com.pettrack.app.data.remote.dto.PetsNearbyRequest
import com.pettrack.app.data.remote.dto.ReportSightingRequest
import com.pettrack.app.data.remote.dto.SetLocationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RpcApi {

    /** Owner sets a pet's GPS point (writes the geography server-side). */
    @POST("rest/v1/rpc/set_pet_location")
    suspend fun setPetLocation(@Body body: SetLocationRequest): Response<Unit>

    /** Lost/searching pets within a radius, filtered — backs the Community screen. */
    @POST("rest/v1/rpc/pets_nearby")
    suspend fun petsNearby(@Body body: PetsNearbyRequest): List<PetNearbyDto>

    /** Report seeing a pet; nudges its search zone server-side. */
    @POST("rest/v1/rpc/report_sighting")
    suspend fun reportSighting(@Body body: ReportSightingRequest): Response<Unit>

    /** Owner contact for a currently-lost pet (privacy-gated server-side). */
    @POST("rest/v1/rpc/get_owner_contact")
    suspend fun getOwnerContact(@Body body: OwnerContactRequest): List<OwnerContactDto>

    /** Community-wide dashboard aggregations (returns a single JSON object). */
    @POST("rest/v1/rpc/dashboard_stats")
    suspend fun dashboardStats(@Body body: DashboardRequest): DashboardStatsDto
}
