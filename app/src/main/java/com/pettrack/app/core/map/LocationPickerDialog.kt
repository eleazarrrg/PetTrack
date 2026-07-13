package com.pettrack.app.core.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Full-screen map dialog to pick a point manually. Avoids the pan-vs-scroll conflict of an
 * inline map inside a scrolling form. Tapping the map drops the pin; "Confirmar" is enabled
 * only once a point is chosen.
 */
@Composable
fun LocationPickerDialog(
    initialCenter: Pair<Double, Double>,
    onConfirm: (Double, Double) -> Unit,
    onDismiss: () -> Unit,
    title: String = "Elegir ubicación",
    initialPoint: Pair<Double, Double>? = null,
) {
    // Stored as two nullable Doubles so rememberSaveable's default saver handles it (survives rotation).
    var pickedLat by rememberSaveable { mutableStateOf(initialPoint?.first) }
    var pickedLng by rememberSaveable { mutableStateOf(initialPoint?.second) }
    val picked: Pair<Double, Double>? =
        if (pickedLat != null && pickedLng != null) pickedLat!! to pickedLng!! else null

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp, end = 16.dp),
                )
                Text(
                    "Toca el mapa para marcar la ubicación.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                )

                OsmMap(
                    center = initialPoint ?: initialCenter,
                    markers = emptyList(),
                    zoom = 14.0,
                    selectedPoint = picked,
                    onMapClick = { lat, lng -> pickedLat = lat; pickedLng = lng },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp)),
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = { picked?.let { onConfirm(it.first, it.second) } },
                        enabled = picked != null,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}
