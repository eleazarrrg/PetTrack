package com.pettrack.app.domain.model

data class DashboardStats(
    val lostVsFound: List<PeriodCount>,
    val bySpecies: List<LabelCount>,
    val byBreed: List<LabelCount>,
    val avgSearchHours: Double?,
    val topZones: List<Zone>,
) {
    val totalPerdidas: Int get() = lostVsFound.sumOf { it.perdida }
    val totalEncontradas: Int get() = lostVsFound.sumOf { it.encontrada }
    val totalEnBusqueda: Int get() = lostVsFound.sumOf { it.enBusqueda }
    val total: Int get() = totalPerdidas + totalEncontradas + totalEnBusqueda
    val avgSearchDays: Double? get() = avgSearchHours?.let { it / 24.0 }
}

data class PeriodCount(
    val period: String,
    val perdida: Int,
    val encontrada: Int,
    val enBusqueda: Int,
)

data class LabelCount(val label: String, val count: Int)

data class Zone(val lat: Double, val lng: Double, val count: Int)
