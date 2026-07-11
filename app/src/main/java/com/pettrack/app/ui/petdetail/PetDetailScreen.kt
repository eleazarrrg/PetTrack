package com.pettrack.app.ui.petdetail

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.pettrack.app.core.map.MapMarker
import com.pettrack.app.core.map.OsmMap
import com.pettrack.app.domain.model.Pet
import com.pettrack.app.domain.model.Sighting

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PetDetailScreen(
    onBack: () -> Unit,
    viewModel: PetDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.pet?.name ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        },
    ) { padding ->
        val pet = state.pet
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding)) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            pet == null -> Box(Modifier.fillMaxSize().padding(padding)) {
                Text(state.error ?: "No se pudo cargar.", Modifier.align(Alignment.Center).padding(24.dp))
            }
            else -> Column(
                modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (state.photoUrls.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.photoUrls) { url ->
                            AsyncImage(model = url, contentDescription = null, modifier = Modifier.size(160.dp))
                        }
                    }
                }

                PetInfo(pet)

                val center = if (pet.latitude != null && pet.longitude != null) pet.latitude to pet.longitude
                else 8.98 to -79.52
                val markers = buildList {
                    if (pet.latitude != null && pet.longitude != null) add(MapMarker(pet.id, pet.latitude, pet.longitude, "Última ubicación"))
                    state.sightings.forEach { s ->
                        if (s.latitude != null && s.longitude != null) add(MapMarker(s.id, s.latitude, s.longitude, "Avistamiento"))
                    }
                }
                OsmMap(center = center, markers = markers, zoom = 14.0, modifier = Modifier.fillMaxWidth().height(220.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = viewModel::openReport, modifier = Modifier.weight(1f)) { Text("Avistamiento") }
                    OutlinedButton(onClick = viewModel::loadContact, modifier = Modifier.weight(1f)) { Text("Contactar") }
                }

                HorizontalDivider()
                Text("Avistamientos", style = MaterialTheme.typography.titleMedium)
                if (state.sightings.isEmpty()) {
                    Text("Sin avistamientos aún.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    state.sightings.forEach { SightingRow(it) }
                }
            }
        }
    }

    // ---- Contact dialog ----
    if (state.showContact) {
        AlertDialog(
            onDismissRequest = viewModel::dismissContact,
            confirmButton = { TextButton(onClick = viewModel::dismissContact) { Text("Cerrar") } },
            title = { Text("Contactar dueño") },
            text = {
                if (state.contactLoading) {
                    CircularProgressIndicator()
                } else {
                    val c = state.contact
                    if (c == null || c.isEmpty) {
                        Text("Contacto no disponible.")
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            c.fullName?.let { Text(it, style = MaterialTheme.typography.titleMedium) }
                            c.phone?.let { p ->
                                TextButton(onClick = {
                                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$p")))
                                }) { Text("Llamar: $p") }
                            }
                            c.email?.let { e ->
                                TextButton(onClick = {
                                    context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$e")))
                                }) { Text("Correo: $e") }
                            }
                        }
                    }
                }
            },
        )
    }

    // ---- Report-sighting dialog ----
    if (state.showReport) {
        AlertDialog(
            onDismissRequest = viewModel::closeReport,
            confirmButton = { TextButton(onClick = viewModel::submitSighting, enabled = !state.reporting) { Text("Enviar") } },
            dismissButton = { TextButton(onClick = viewModel::closeReport) { Text("Cancelar") } },
            title = { Text("Reportar avistamiento") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = state.note,
                        onValueChange = viewModel::onNote,
                        label = { Text("Nota (dónde la viste)") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedButton(
                        onClick = {
                            if (locationPermission.status.isGranted) viewModel.captureSightingLocation()
                            else locationPermission.launchPermissionRequest()
                        },
                        enabled = !state.capturingLocation,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            when {
                                state.capturingLocation -> "Obteniendo ubicación…"
                                state.sightLat != null -> "Ubicación lista ✓"
                                else -> "Capturar ubicación (GPS)"
                            },
                        )
                    }
                    Text(
                        "Si no capturas, se usará la última ubicación conocida.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    state.reportError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                }
            },
        )
    }
}

@Composable
private fun PetInfo(pet: Pet) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(pet.name, style = MaterialTheme.typography.headlineSmall)
            Text(buildString { append(pet.species); pet.breed?.let { append(" · $it") } })
            Text("Estado: ${pet.status.label}")
            pet.size?.let { Text("Tamaño: ${it.label}") }
            pet.approxAge?.let { Text("Edad: $it") }
            pet.color?.let { Text("Color: $it") }
            pet.distinguishingMarks?.let { Text("Señas: $it") }
            Text("Collar/chip: " + if (pet.hasCollarChip) "Sí" + (pet.chipNumber?.let { " ($it)" } ?: "") else "No")
        }
    }
}

@Composable
private fun SightingRow(sighting: Sighting) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Text(sighting.sightedAt?.take(16)?.replace("T", " ") ?: "Fecha desconocida", style = MaterialTheme.typography.labelMedium)
            Text(sighting.note ?: "(sin nota)", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
