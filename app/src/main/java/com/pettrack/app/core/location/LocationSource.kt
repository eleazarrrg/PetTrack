package com.pettrack.app.core.location

/** Abstraction over device location so ViewModels can be unit-tested without Play Services. */
interface LocationSource {
    suspend fun currentLatLng(): Pair<Double, Double>?
}
