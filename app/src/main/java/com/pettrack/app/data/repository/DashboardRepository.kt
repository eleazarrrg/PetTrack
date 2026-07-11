package com.pettrack.app.data.repository

import com.pettrack.app.core.di.IoDispatcher
import com.pettrack.app.data.remote.api.RpcApi
import com.pettrack.app.data.remote.dto.DashboardRequest
import com.pettrack.app.data.remote.dto.DashboardStatsDto
import com.pettrack.app.domain.model.DashboardStats
import com.pettrack.app.domain.model.LabelCount
import com.pettrack.app.domain.model.PeriodCount
import com.pettrack.app.domain.model.Zone
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    private val rpcApi: RpcApi,
    @IoDispatcher private val io: CoroutineDispatcher,
) {
    suspend fun stats(): Result<DashboardStats> = runCatching {
        withContext(io) { rpcApi.dashboardStats(DashboardRequest()).toDomain() }
    }
}

private fun DashboardStatsDto.toDomain() = DashboardStats(
    lostVsFound = lostVsFound.map {
        PeriodCount(it.period ?: "", it.perdida, it.encontrada, it.enBusqueda)
    },
    bySpecies = bySpecies.map { LabelCount(it.species ?: "—", it.count) },
    byBreed = byBreed.map { LabelCount(it.breed ?: "—", it.count) },
    avgSearchHours = avgSearchHours,
    topZones = topZones.map { Zone(it.latitude, it.longitude, it.count) },
)
