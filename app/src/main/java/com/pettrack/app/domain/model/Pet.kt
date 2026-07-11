package com.pettrack.app.domain.model

enum class PetStatus(val api: String, val label: String) {
    PERDIDA("perdida", "Perdida"),
    ENCONTRADA("encontrada", "Encontrada"),
    EN_BUSQUEDA("en_busqueda", "En búsqueda");

    companion object {
        fun from(api: String?): PetStatus = entries.firstOrNull { it.api == api } ?: EN_BUSQUEDA
    }
}

enum class PetSize(val api: String, val label: String) {
    PEQUENO("pequeno", "Pequeño"),
    MEDIANO("mediano", "Mediano"),
    GRANDE("grande", "Grande");

    companion object {
        fun from(api: String?): PetSize? = entries.firstOrNull { it.api == api }
    }
}

data class Pet(
    val id: String,
    val ownerId: String,
    val name: String,
    val species: String,
    val breed: String?,
    val approxAge: String?,
    val color: String?,
    val size: PetSize?,
    val distinguishingMarks: String?,
    val hasCollarChip: Boolean,
    val chipNumber: String?,
    val status: PetStatus,
    val lostAt: String?,
    val latitude: Double?,
    val longitude: Double?,
)

/** Values captured by the report form (before mapping to insert/put DTOs). */
data class PetInput(
    val name: String,
    val species: String,
    val breed: String?,
    val approxAge: String?,
    val color: String?,
    val size: PetSize?,
    val distinguishingMarks: String?,
    val hasCollarChip: Boolean,
    val chipNumber: String?,
    val status: PetStatus,
    val lostAt: String?,
)

/** In-memory picked photo (bytes + mime) ready to upload to Storage. */
data class PhotoBytes(
    val bytes: ByteArray,
    val mime: String,
) {
    val ext: String get() = when (mime) {
        "image/png" -> "png"
        "image/webp" -> "webp"
        else -> "jpg"
    }
}
