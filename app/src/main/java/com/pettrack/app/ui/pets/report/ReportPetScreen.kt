package com.pettrack.app.ui.pets.report

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.pettrack.app.domain.model.PetSize
import com.pettrack.app.domain.model.PetStatus

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class, ExperimentalLayoutApi::class)
@Composable
fun ReportPetScreen(
    onSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: ReportPetViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var pickedUri by remember { mutableStateOf<Uri?>(null) }

    val photoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            pickedUri = uri
            val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            if (bytes != null) viewModel.onPhotoPicked(bytes, mime, uri.lastPathSegment ?: "foto")
        }
    }

    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(state.success) { if (state.success) onSaved() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEdit) "Editar mascota" else "Reportar mascota") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(state.name, viewModel::onName, label = { Text("Nombre*") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.species, viewModel::onSpecies, label = { Text("Especie* (perro, gato…)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.breed, viewModel::onBreed, label = { Text("Raza") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.approxAge, viewModel::onApproxAge, label = { Text("Edad aproximada") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.color, viewModel::onColor, label = { Text("Color") }, singleLine = true, modifier = Modifier.fillMaxWidth())

            Text("Tamaño", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PetSize.entries.forEach { sz ->
                    FilterChip(
                        selected = state.size == sz,
                        onClick = { viewModel.onSize(if (state.size == sz) null else sz) },
                        label = { Text(sz.label) },
                    )
                }
            }

            OutlinedTextField(state.distinguishingMarks, viewModel::onMarks, label = { Text("Señas particulares") }, modifier = Modifier.fillMaxWidth())

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = state.hasCollarChip, onCheckedChange = viewModel::onCollarChip)
                Text("  Tiene collar/chip", style = MaterialTheme.typography.bodyMedium)
            }
            if (state.hasCollarChip) {
                OutlinedTextField(state.chipNumber, viewModel::onChipNumber, label = { Text("Número de chip") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }

            HorizontalDivider()
            Text("Estado", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PetStatus.entries.forEach { st ->
                    FilterChip(
                        selected = state.status == st,
                        onClick = { viewModel.onStatus(st) },
                        label = { Text(st.label) },
                    )
                }
            }

            OutlinedButton(onClick = viewModel::stampLostNow, modifier = Modifier.fillMaxWidth()) {
                Text(if (state.lostAt == null) "Marcar hora de pérdida (ahora)" else "Perdida: ${state.lostAt}")
            }

            HorizontalDivider()
            OutlinedButton(
                onClick = {
                    if (locationPermission.status.isGranted) viewModel.captureLocation()
                    else locationPermission.launchPermissionRequest()
                },
                enabled = !state.capturingLocation,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    when {
                        state.capturingLocation -> "Obteniendo ubicación…"
                        state.latitude != null -> "Ubicación: %.4f, %.4f".format(state.latitude, state.longitude)
                        else -> "Capturar ubicación (GPS)"
                    },
                )
            }

            OutlinedButton(
                onClick = { photoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.photoName == null) "Seleccionar foto" else "Foto: ${state.photoName}")
            }
            pickedUri?.let { uri ->
                AsyncImage(model = uri, contentDescription = "Foto seleccionada", modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
            }

            if (state.error != null) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(onClick = viewModel::submit, enabled = !state.loading, modifier = Modifier.fillMaxWidth()) {
                if (state.loading) CircularProgressIndicator(Modifier.padding(end = 8.dp))
                Text(if (state.isEdit) "Guardar cambios" else "Reportar")
            }
        }
    }
}
