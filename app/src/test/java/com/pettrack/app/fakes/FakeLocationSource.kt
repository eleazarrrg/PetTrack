package com.pettrack.app.fakes

import com.pettrack.app.core.location.LocationSource

class FakeLocationSource(var latLng: Pair<Double, Double>? = null) : LocationSource {
    override suspend fun currentLatLng(): Pair<Double, Double>? = latLng
}
