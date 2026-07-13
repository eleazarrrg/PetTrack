package com.pettrack.app.ui.pets.report

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pettrack.app.core.common.authErrorMessage
import com.pettrack.app.core.location.LocationSource
import com.pettrack.app.data.repository.PetRepository
import com.pettrack.app.domain.model.PetInput
import com.pettrack.app.domain.model.PetSize
import com.pettrack.app.domain.model.PetStatus
import com.pettrack.app.domain.model.PhotoBytes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class ReportPetUiState(
    val name: String = "",
    val species: String = "",
    val breed: String = "",
    val approxAge: String = "",
    val color: String = "",
    val size: PetSize? = null,
    val distinguishingMarks: String = "",
    val hasCollarChip: Boolean = false,
    val chipNumber: String = "",
    val status: PetStatus = PetStatus.PERDIDA,
    val lostAt: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val photoName: String? = null,
    val capturingLocation: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val isEdit: Boolean = false,
)

@HiltViewModel
class ReportPetViewModel @Inject constructor(
    private val repository: PetRepository,
    private val locationProvider: LocationSource,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val petId: String? = savedStateHandle["petId"]
    private var photo: PhotoBytes? = null

    private val _state = MutableStateFlow(ReportPetUiState(isEdit = petId != null))
    val state: StateFlow<ReportPetUiState> = _state.asStateFlow()

    init {
        petId?.let { loadPet(it) }
    }

    fun onName(v: String) = _state.update { it.copy(name = v, error = null) }
    fun onSpecies(v: String) = _state.update { it.copy(species = v, error = null) }
    fun onBreed(v: String) = _state.update { it.copy(breed = v) }
    fun onApproxAge(v: String) = _state.update { it.copy(approxAge = v) }
    fun onColor(v: String) = _state.update { it.copy(color = v) }
    fun onSize(v: PetSize?) = _state.update { it.copy(size = v) }
    fun onMarks(v: String) = _state.update { it.copy(distinguishingMarks = v) }
    fun onCollarChip(v: Boolean) = _state.update { it.copy(hasCollarChip = v) }
    fun onChipNumber(v: String) = _state.update { it.copy(chipNumber = v) }
    fun onStatus(v: PetStatus) = _state.update { it.copy(status = v) }
    fun stampLostNow() = _state.update { it.copy(lostAt = nowIso()) }

    fun onPhotoPicked(bytes: ByteArray, mime: String, name: String) {
        photo = PhotoBytes(bytes, mime)
        _state.update { it.copy(photoName = name) }
    }

    /** Manual pin from the map picker (e.g. "se perdió en la playa" aunque ya estés en casa). */
    fun setLocation(lat: Double, lng: Double) =
        _state.update { it.copy(latitude = lat, longitude = lng, error = null) }

    fun captureLocation() {
        viewModelScope.launch {
            _state.update { it.copy(capturingLocation = true, error = null) }
            val latLng = try {
                locationProvider.currentLatLng()
            } catch (_: Exception) {
                null
            }
            _state.update {
                it.copy(
                    capturingLocation = false,
                    latitude = latLng?.first ?: it.latitude,
                    longitude = latLng?.second ?: it.longitude,
                    error = if (latLng == null) "No se pudo obtener la ubicación GPS." else it.error,
                )
            }
        }
    }

    fun submit() {
        val s = _state.value
        val problem = when {
            s.name.isBlank() -> "Ingresa el nombre de la mascota."
            s.species.isBlank() -> "Ingresa la especie."
            else -> null
        }
        if (problem != null) {
            _state.update { it.copy(error = problem) }
            return
        }
        val input = PetInput(
            name = s.name.trim(),
            species = s.species.trim(),
            breed = s.breed.trim().ifBlank { null },
            approxAge = s.approxAge.trim().ifBlank { null },
            color = s.color.trim().ifBlank { null },
            size = s.size,
            distinguishingMarks = s.distinguishingMarks.trim().ifBlank { null },
            hasCollarChip = s.hasCollarChip,
            chipNumber = s.chipNumber.trim().ifBlank { null },
            status = s.status,
            lostAt = s.lostAt,
        )
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            val result = if (petId != null) {
                repository.updatePet(petId, input, s.latitude, s.longitude, photo).map { }
            } else {
                repository.createPet(input, s.latitude, s.longitude, photo).map { }
            }
            result
                .onSuccess { _state.update { it.copy(loading = false, success = true) } }
                .onFailure { e -> _state.update { it.copy(loading = false, error = authErrorMessage(e)) } }
        }
    }

    private fun loadPet(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            repository.getPet(id)
                .onSuccess { pet ->
                    _state.update {
                        it.copy(
                            loading = false,
                            name = pet.name,
                            species = pet.species,
                            breed = pet.breed.orEmpty(),
                            approxAge = pet.approxAge.orEmpty(),
                            color = pet.color.orEmpty(),
                            size = pet.size,
                            distinguishingMarks = pet.distinguishingMarks.orEmpty(),
                            hasCollarChip = pet.hasCollarChip,
                            chipNumber = pet.chipNumber.orEmpty(),
                            status = pet.status,
                            lostAt = pet.lostAt,
                            latitude = pet.latitude,
                            longitude = pet.longitude,
                        )
                    }
                }
                .onFailure { e -> _state.update { it.copy(loading = false, error = authErrorMessage(e)) } }
        }
    }

    private fun nowIso(): String =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).format(Date())
}
