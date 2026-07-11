package com.pettrack.app.ui.petdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pettrack.app.core.common.authErrorMessage
import com.pettrack.app.core.location.LocationSource
import com.pettrack.app.data.repository.CommunityRepository
import com.pettrack.app.data.repository.PetRepository
import com.pettrack.app.domain.model.OwnerContact
import com.pettrack.app.domain.model.Pet
import com.pettrack.app.domain.model.Sighting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PetDetailUiState(
    val loading: Boolean = true,
    val pet: Pet? = null,
    val photoUrls: List<String> = emptyList(),
    val sightings: List<Sighting> = emptyList(),
    val error: String? = null,
    // contact dialog
    val showContact: Boolean = false,
    val contactLoading: Boolean = false,
    val contact: OwnerContact? = null,
    // report-sighting dialog
    val showReport: Boolean = false,
    val note: String = "",
    val sightLat: Double? = null,
    val sightLng: Double? = null,
    val capturingLocation: Boolean = false,
    val reporting: Boolean = false,
    val reportError: String? = null,
)

@HiltViewModel
class PetDetailViewModel @Inject constructor(
    private val petRepo: PetRepository,
    private val community: CommunityRepository,
    private val location: LocationSource,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val petId: String = checkNotNull(savedStateHandle["petId"])

    private val _state = MutableStateFlow(PetDetailUiState())
    val state: StateFlow<PetDetailUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            petRepo.getPet(petId)
                .onSuccess { pet ->
                    val photos = community.photoUrls(petId).getOrDefault(emptyList())
                    val sightings = community.sightings(petId).getOrDefault(emptyList())
                    _state.update { it.copy(loading = false, pet = pet, photoUrls = photos, sightings = sightings) }
                }
                .onFailure { e -> _state.update { it.copy(loading = false, error = authErrorMessage(e)) } }
        }
    }

    // ---- contact ----
    fun loadContact() {
        viewModelScope.launch {
            _state.update { it.copy(showContact = true, contactLoading = true) }
            community.ownerContact(petId)
                .onSuccess { c -> _state.update { it.copy(contactLoading = false, contact = c) } }
                .onFailure { _state.update { it.copy(contactLoading = false, contact = OwnerContact(null, null, null)) } }
        }
    }

    fun dismissContact() = _state.update { it.copy(showContact = false) }

    // ---- report sighting ----
    fun openReport() = _state.update { it.copy(showReport = true, note = "", sightLat = null, sightLng = null, reportError = null) }
    fun closeReport() = _state.update { it.copy(showReport = false) }
    fun onNote(v: String) = _state.update { it.copy(note = v) }

    fun captureSightingLocation() {
        viewModelScope.launch {
            _state.update { it.copy(capturingLocation = true) }
            val latLng = try { location.currentLatLng() } catch (_: Exception) { null }
            _state.update {
                it.copy(
                    capturingLocation = false,
                    sightLat = latLng?.first ?: it.sightLat,
                    sightLng = latLng?.second ?: it.sightLng,
                )
            }
        }
    }

    fun submitSighting() {
        val s = _state.value
        // Use captured GPS, else fall back to the pet's last known location.
        val lat = s.sightLat ?: s.pet?.latitude
        val lng = s.sightLng ?: s.pet?.longitude
        if (lat == null || lng == null) {
            _state.update { it.copy(reportError = "Captura una ubicación para el avistamiento.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(reporting = true, reportError = null) }
            community.reportSighting(petId, lat, lng, s.note)
                .onSuccess {
                    _state.update { it.copy(reporting = false, showReport = false) }
                    load()
                }
                .onFailure { e -> _state.update { it.copy(reporting = false, reportError = authErrorMessage(e)) } }
        }
    }
}
