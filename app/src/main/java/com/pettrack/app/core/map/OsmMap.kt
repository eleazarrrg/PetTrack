package com.pettrack.app.core.map

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pettrack.app.R
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

/** Ciudad de Panamá — centro por defecto cuando no hay ubicación conocida. */
val DEFAULT_MAP_CENTER = 8.98 to -79.52

data class MapMarker(
    val id: String,
    val lat: Double,
    val lng: Double,
    val title: String,
)

private data class MapSnapshot(
    val center: Pair<Double, Double>,
    val markers: List<MapMarker>,
    val radiusMeters: Double?,
    val selectedPoint: Pair<Double, Double>?,
    // Included so toggling tap-to-pick on/off re-runs update() and (de)registers the events overlay.
    val hasClickHandler: Boolean,
)

/**
 * Minimal OpenStreetMap (osmdroid) map for Compose.
 *
 * IMPORTANT: the map is only re-centered when [center] actually changes (not on every
 * recomposition) so the user's panning is preserved — otherwise unrelated state updates
 * (e.g. the notifications badge polling) would keep yanking the map back to center.
 *
 * The MapView is forced to MATCH_PARENT and clipped to its bounds because osmdroid draws
 * tiles beyond its layout box (world repetition), which otherwise bleeds over sibling
 * composables — visible when the map lives inside a scrolling Column without a clip.
 *
 * When [onMapClick] is provided, tapping the map reports the tapped coordinates and
 * [selectedPoint] draws a pin there — used by the location picker and the community search.
 */
@Composable
fun OsmMap(
    center: Pair<Double, Double>,
    markers: List<MapMarker>,
    modifier: Modifier = Modifier,
    zoom: Double = 13.0,
    radiusMeters: Double? = null,
    selectedPoint: Pair<Double, Double>? = null,
    onMarkerClick: (String) -> Unit = {},
    onMapClick: ((Double, Double) -> Unit)? = null,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        MapView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            setTilesScaledToDpi(true)
            setUseDataConnection(true)
            // Stop the world from repeating so tiles don't render outside the map box.
            isHorizontalMapRepetitionEnabled = false
            isVerticalMapRepetitionEnabled = false
            setMinZoomLevel(3.0)
            controller.setZoom(zoom)
            controller.setCenter(GeoPoint(center.first, center.second))
        }
    }

    // Keep the latest click callback so the (remembered) overlay never calls a stale lambda.
    val currentOnMapClick by rememberUpdatedState(onMapClick)
    val eventsOverlay = remember {
        MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                val cb = currentOnMapClick ?: return false
                if (p != null) cb(p.latitude, p.longitude)
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean = false
        })
    }

    // Tracks what has already been applied so update() is a no-op when nothing changed.
    val applied = remember { mutableStateOf<MapSnapshot?>(null) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier.clipToBounds(),
        update = { map ->
            val snapshot = MapSnapshot(center, markers, radiusMeters, selectedPoint, onMapClick != null)
            if (applied.value != snapshot) {
                // Only re-center when the center itself changed (keeps user panning).
                if (applied.value?.center != center) {
                    map.controller.setCenter(GeoPoint(center.first, center.second))
                }
                map.overlays.clear()

                // Add first so marker taps (added later, drawn on top) win over map taps.
                if (onMapClick != null) map.overlays.add(eventsOverlay)

                radiusMeters?.let { r ->
                    val circle = Polygon().apply {
                        points = Polygon.pointsAsCircle(GeoPoint(center.first, center.second), r)
                        fillPaint.color = 0x331B6C5A.toInt()
                        outlinePaint.color = 0xFF1B6C5A.toInt()
                        outlinePaint.strokeWidth = 3f
                    }
                    map.overlays.add(circle)
                }

                val pin = ContextCompat.getDrawable(map.context, R.drawable.ic_map_pin)
                markers.forEach { m ->
                    val marker = Marker(map).apply {
                        position = GeoPoint(m.lat, m.lng)
                        title = m.title
                        icon = pin
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        setOnMarkerClickListener { mk, _ ->
                            onMarkerClick(m.id)
                            mk.showInfoWindow()
                            true
                        }
                    }
                    map.overlays.add(marker)
                }

                selectedPoint?.let { sp ->
                    val chosen = Marker(map).apply {
                        position = GeoPoint(sp.first, sp.second)
                        title = "Ubicación elegida"
                        icon = pin
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    map.overlays.add(chosen)
                }

                map.invalidate()
                applied.value = snapshot
            }
        },
    )
}
