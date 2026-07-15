package com.pettrack.app.ui.community

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.pettrack.app.core.map.MapMarker
import com.pettrack.app.core.map.OsmMap
import com.pettrack.app.domain.model.NearbyPet
import com.pettrack.app.domain.model.PetStatus

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CommunityScreen(
    onOpenPet: (String) -> Unit,
    unreadCount: Int = 0,
    onOpenNotifications: () -> Unit = {},
    viewModel: CommunityViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var filtersExpanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comunidad") },
                actions = {
                    IconButton(onClick = onOpenNotifications) {
                        BadgedBox(badge = { if (unreadCount > 0) Badge { Text(unreadCount.toString()) } }) {
                            Icon(Icons.Filled.Notifications, contentDescription = "Notificaciones")
                        }
                    }
                },
            )
        },
    ) { padding ->
        // One scrollable list: filters + map are header items, so scrollean y la lista de mascotas
        // aprovecha toda la pantalla. El mapa sigue siendo arrastrable (OsmMap evita que el scroll
        // le robe el gesto).
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                // Collapsible filters: keep them out of the way so the list below the map isn't cramped.
                val filterSummary = buildString {
                    if (state.species.isNotBlank()) append(state.species.trim()).append(" · ")
                    append("${state.radiusKm} km · ")
                    append(state.status?.label ?: "Todas")
                    append(" · ").append(state.dateFilter.label)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { filtersExpanded = !filtersExpanded }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(Icons.Filled.Tune, contentDescription = null)
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Filtros", style = MaterialTheme.typography.titleSmall)
                        if (!filtersExpanded) {
                            Text(
                                filterSummary,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    Icon(
                        if (filtersExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (filtersExpanded) "Ocultar filtros" else "Mostrar filtros",
                    )
                }

                if (filtersExpanded) {
                    OutlinedTextField(
                        value = state.species,
                        onValueChange = viewModel::setSpecies,
                        label = { Text("Especie (perro, gato…)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { viewModel.applySpecies() }),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Text("Radio", style = MaterialTheme.typography.labelMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RADIUS_OPTIONS.forEach { km ->
                            FilterChip(
                                selected = state.radiusKm == km,
                                onClick = { viewModel.setRadius(km) },
                                label = { Text("$km km") },
                            )
                        }
                    }

                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(state.status == null, { viewModel.setStatus(null) }, label = { Text("Todas") })
                        FilterChip(state.status == PetStatus.PERDIDA, { viewModel.setStatus(PetStatus.PERDIDA) }, label = { Text("Perdida") })
                        FilterChip(state.status == PetStatus.EN_BUSQUEDA, { viewModel.setStatus(PetStatus.EN_BUSQUEDA) }, label = { Text("En búsqueda") })
                    }

                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DateFilter.entries.forEach { df ->
                            FilterChip(
                                selected = state.dateFilter == df,
                                onClick = { viewModel.setDateFilter(df) },
                                label = { Text(df.label) },
                            )
                        }
                    }

                    TextButton(onClick = viewModel::useMyLocation) {
                        Icon(Icons.Filled.MyLocation, contentDescription = null)
                        Text(if (state.usingMyLocation) "  Usando mi ubicación" else "  Usar mi ubicación")
                    }
                }

                Text(
                    "Toca el mapa para buscar en otra zona.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                }
            }

            item {
                OsmMap(
                    center = state.center,
                    markers = state.pets.map { MapMarker(it.id, it.latitude, it.longitude, it.name) },
                    radiusMeters = state.radiusKm * 1000.0,
                    onMarkerClick = onOpenPet,
                    onMapClick = { lat, lng -> viewModel.setCenter(lat, lng) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                )
            }

            when {
                state.loading -> item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) { CircularProgressIndicator() }
                }
                state.error != null -> item {
                    Text(
                        state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                    )
                }
                state.pets.isEmpty() -> item {
                    Text(
                        "No hay mascotas reportadas en esta zona.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                    )
                }
                else -> items(state.pets, key = { it.id }) { pet ->
                    NearbyPetCard(
                        pet,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = { onOpenPet(pet.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun NearbyPetCard(pet: NearbyPet, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedCard(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (pet.photoUrl != null) {
                AsyncImage(model = pet.photoUrl, contentDescription = null, modifier = Modifier.size(56.dp))
            } else {
                Icon(Icons.Filled.Pets, contentDescription = null, modifier = Modifier.size(56.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(pet.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    buildString { append(pet.species); pet.breed?.let { append(" · $it") } },
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "${pet.status.label} · ${formatDistance(pet.distanceM)}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

private fun formatDistance(meters: Double): String =
    if (meters < 1000) "%.0f m".format(meters) else "%.1f km".format(meters / 1000)
