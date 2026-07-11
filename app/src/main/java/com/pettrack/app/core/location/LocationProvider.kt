package com.pettrack.app.core.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) : LocationSource {
    private val client by lazy { LocationServices.getFusedLocationProviderClient(context) }

    /** Requires ACCESS_FINE/COARSE_LOCATION already granted by the caller. */
    @SuppressLint("MissingPermission")
    override suspend fun currentLatLng(): Pair<Double, Double>? {
        val current = client.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token,
        ).await()
        val loc = current ?: client.lastLocation.await()
        return loc?.let { it.latitude to it.longitude }
    }
}
