package com.pettrack.app.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pettrack.app.core.map.MapMarker
import com.pettrack.app.core.map.OsmMap
import com.pettrack.app.domain.model.DashboardStats
import com.pettrack.app.ui.dashboard.components.GroupedColumns
import com.pettrack.app.ui.dashboard.components.HorizontalBars
import com.pettrack.app.ui.dashboard.components.KpiTile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("Dashboard") }) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.error != null -> Text(
                    state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                )
                state.stats != null -> DashboardContent(state.stats!!)
            }
        }
    }
}

@Composable
private fun DashboardContent(stats: DashboardStats) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Resumen", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiTile(stats.total.toString(), "Total", Modifier.weight(1f))
            KpiTile(stats.totalPerdidas.toString(), "Perdidas", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiTile(stats.totalEncontradas.toString(), "Encontradas", Modifier.weight(1f))
            KpiTile(stats.totalEnBusqueda.toString(), "En búsqueda", Modifier.weight(1f))
        }
        KpiTile(
            value = stats.avgSearchDays?.let { "%.1f".format(it) } ?: "—",
            label = "Días promedio de búsqueda",
            modifier = Modifier.fillMaxWidth(),
        )

        Text("Perdidas vs Encontradas por mes", style = MaterialTheme.typography.titleMedium)
        if (stats.lostVsFound.isEmpty()) {
            Text("Sin datos.", style = MaterialTheme.typography.bodyMedium)
        } else {
            GroupedColumns(stats.lostVsFound, Modifier.fillMaxWidth())
        }

        Text("Por especie", style = MaterialTheme.typography.titleMedium)
        if (stats.bySpecies.isEmpty()) {
            Text("Sin datos.", style = MaterialTheme.typography.bodyMedium)
        } else {
            HorizontalBars(stats.bySpecies.take(6), Modifier.fillMaxWidth())
        }

        Text("Por raza", style = MaterialTheme.typography.titleMedium)
        if (stats.byBreed.isEmpty()) {
            Text("Sin datos.", style = MaterialTheme.typography.bodyMedium)
        } else {
            HorizontalBars(stats.byBreed.take(6), Modifier.fillMaxWidth())
        }

        Text("Zonas con más reportes", style = MaterialTheme.typography.titleMedium)
        val zones = stats.topZones
        val center = if (zones.isNotEmpty()) {
            zones.sumOf { it.lat } / zones.size to zones.sumOf { it.lng } / zones.size
        } else {
            8.98 to -79.52
        }
        OsmMap(
            center = center,
            markers = zones.mapIndexed { i, z -> MapMarker("zone_$i", z.lat, z.lng, "${z.count} reportes") },
            zoom = 12.0,
            modifier = Modifier.fillMaxWidth().height(240.dp),
        )
    }
}
