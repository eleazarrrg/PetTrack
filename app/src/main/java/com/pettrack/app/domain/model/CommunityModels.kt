package com.pettrack.app.domain.model

data class NearbyPet(
    val id: String,
    val ownerId: String,
    val name: String,
    val species: String,
    val breed: String?,
    val status: PetStatus,
    val latitude: Double,
    val longitude: Double,
    val distanceM: Double,
    val photoUrl: String?,
    val lostAt: String?,
)

data class Sighting(
    val id: String,
    val petId: String,
    val note: String?,
    val sightedAt: String?,
    val latitude: Double?,
    val longitude: Double?,
)

data class OwnerContact(
    val fullName: String?,
    val phone: String?,
    val email: String?,
) {
    val isEmpty: Boolean get() = fullName == null && phone == null && email == null
}
