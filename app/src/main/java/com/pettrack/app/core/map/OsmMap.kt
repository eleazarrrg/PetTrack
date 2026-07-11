package com.pettrack.app.core.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pettrack.app.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

data class MapMarker(
    val id: String,
    val lat: Double,
    val lng: Double,
    val title: String,
)

/**
 * Minimal OpenStreetMap (osmdroid) map for Compose: renders markers and an optional
 * search-radius circle around [center]. `setTilesScaledToDpi` keeps tiles legible on
 * high-density screens (fixes the tiny/blurry tiles seen on the emulator).
 */
@Composable
fun OsmMap(
    center: Pair<Double, Double>,
    markers: List<MapMarker>,
    modifier: Modifier = Modifier,
    zoom: Double = 13.0,
    radiusMeters: Double? = null,
    onMarkerClick: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            setTilesScaledToDpi(true)
            setUseDataConnection(true)
            controller.setZoom(zoom)
        }
    }

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
        modifier = modifier,
        update = { map ->
            val centerPoint = GeoPoint(center.first, center.second)
            map.controller.setCenter(centerPoint)
            map.overlays.clear()

            radiusMeters?.let { r ->
                val circle = Polygon().apply {
                    points = Polygon.pointsAsCircle(centerPoint, r)
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
            map.invalidate()
        },
    )
}
