package com.pettrack.app.ui.dashboard

import com.pettrack.app.data.remote.dto.BreedCountDto
import com.pettrack.app.data.remote.dto.DashboardStatsDto
import com.pettrack.app.data.remote.dto.LostVsFoundDto
import com.pettrack.app.data.remote.dto.SpeciesCountDto
import com.pettrack.app.data.remote.dto.ZoneDto
import com.pettrack.app.data.repository.DashboardRepository
import com.pettrack.app.fakes.FakeRpcApi
import com.pettrack.app.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val rpcApi = FakeRpcApi()

    private fun viewModel() = DashboardViewModel(DashboardRepository(rpcApi, mainRule.dispatcher))

    @Test
    fun load_mapsTotalsAndAverageDays() = runTest {
        rpcApi.dashboardResult = DashboardStatsDto(
            lostVsFound = listOf(
                LostVsFoundDto("2026-07-01", perdida = 2, encontrada = 1, enBusqueda = 0),
                LostVsFoundDto("2026-06-01", perdida = 0, encontrada = 1, enBusqueda = 1),
            ),
            bySpecies = listOf(SpeciesCountDto("perro", 3)),
            byBreed = listOf(BreedCountDto("Labrador", 1)),
            avgSearchHours = 48.0,
            topZones = listOf(ZoneDto(8.98, -79.52, 2)),
        )
        val vm = viewModel()
        val stats = requireNotNull(vm.state.value.stats)
        assertEquals(2, stats.totalPerdidas)
        assertEquals(2, stats.totalEncontradas)
        assertEquals(1, stats.totalEnBusqueda)
        assertEquals(5, stats.total)
        assertEquals(2.0, stats.avgSearchDays!!, 0.001)
        assertEquals(1, stats.bySpecies.size)
        assertEquals(1, stats.topZones.size)
    }
}
