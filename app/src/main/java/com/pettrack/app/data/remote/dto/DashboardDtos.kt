package com.pettrack.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Body for POST /rest/v1/rpc/dashboard_stats (nulls omitted → server defaults). */
@Serializable
data class DashboardRequest(
    val granularity: String = "month",
)

@Serializable
data class DashboardStatsDto(
    val range: RangeDto? = null,
    @SerialName("lost_vs_found") val lostVsFound: List<LostVsFoundDto> = emptyList(),
    @SerialName("by_species") val bySpecies: List<SpeciesCountDto> = emptyList(),
    @SerialName("by_breed") val byBreed: List<BreedCountDto> = emptyList(),
    @SerialName("avg_search_hours") val avgSearchHours: Double? = null,
    @SerialName("top_zones") val topZones: List<ZoneDto> = emptyList(),
)

@Serializable
data class RangeDto(
    val from: String? = null,
    val to: String? = null,
    val granularity: String? = null,
)

@Serializable
data class LostVsFoundDto(
    val period: String? = null,
    val perdida: Int = 0,
    val encontrada: Int = 0,
    @SerialName("en_busqueda") val enBusqueda: Int = 0,
)

@Serializable
data class SpeciesCountDto(
    val species: String? = null,
    val count: Int = 0,
)

@Serializable
data class BreedCountDto(
    val breed: String? = null,
    val count: Int = 0,
)

@Serializable
data class ZoneDto(
    val latitude: Double,
    val longitude: Double,
    val count: Int = 0,
)
