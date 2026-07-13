package com.pettrack.app.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pettrack.app.core.common.authErrorMessage
import com.pettrack.app.core.location.LocationSource
import com.pettrack.app.core.map.DEFAULT_MAP_CENTER
import com.pettrack.app.data.repository.CommunityRepository
import com.pettrack.app.domain.model.NearbyPet
import com.pettrack.app.domain.model.PetStatus
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

enum class DateFilter(val label: String, val days: Int?) {
    ANY("Cualquiera", null),
    WEEK("Última semana", 7),
    MONTH("Último mes", 30),
}

data class CommunityUiState(
    val loading: Boolean = true,
    val pets: List<NearbyPet> = emptyList(),
    val error: String? = null,
    val center: Pair<Double, Double> = DEFAULT_CENTER,
    val usingMyLocation: Boolean = false,
    val radiusKm: Int = 5,
    val species: String = "",
    val status: PetStatus? = null,
    val dateFilter: DateFilter = DateFilter.ANY,
) {
    companion object {
        // Ciudad de Panamá — fallback so the demo shows the seeded pets.
        val DEFAULT_CENTER = DEFAULT_MAP_CENTER
    }
}

val RADIUS_OPTIONS = listOf(1, 5, 10, 25)

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val community: CommunityRepository,
    private val location: LocationSource,
) : ViewModel() {

    private val _state = MutableStateFlow(CommunityUiState())
    val state: StateFlow<CommunityUiState> = _state.asStateFlow()

    init { load() }

    fun setRadius(km: Int) { _state.update { it.copy(radiusKm = km) }; load() }
    fun setSpecies(v: String) { _state.update { it.copy(species = v) } }
    fun applySpecies() = load()
    fun setStatus(s: PetStatus?) { _state.update { it.copy(status = s) }; load() }
    fun setDateFilter(f: DateFilter) { _state.update { it.copy(dateFilter = f) }; load() }

    /** Tap-to-search: fija el centro de búsqueda en el punto que el usuario tocó en el mapa. */
    fun setCenter(lat: Double, lng: Double) {
        _state.update { it.copy(center = lat to lng, usingMyLocation = false) }
        load()
    }

    fun useMyLocation() {
        viewModelScope.launch {
            val latLng = try { location.currentLatLng() } catch (_: Exception) { null }
            if (latLng != null) {
                _state.update { it.copy(center = latLng, usingMyLocation = true) }
                load()
            } else {
                _state.update { it.copy(error = "No se pudo obtener tu ubicación GPS.") }
            }
        }
    }

    // Monotonic tag: rapid searches (tap-to-search, chips) fire overlapping loads; only the latest
    // one is allowed to write results, so a slow earlier response can't clobber a newer search.
    private var loadGeneration = 0

    fun load() {
        val s = _state.value
        val gen = ++loadGeneration
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            community.nearby(
                lat = s.center.first,
                lng = s.center.second,
                radiusM = s.radiusKm * 1000.0,
                species = s.species,
                status = s.status,
                from = fromIso(s.dateFilter),
            )
                .onSuccess { pets -> if (gen == loadGeneration) _state.update { it.copy(loading = false, pets = pets) } }
                .onFailure { e -> if (gen == loadGeneration) _state.update { it.copy(loading = false, error = authErrorMessage(e)) } }
        }
    }

    private fun fromIso(filter: DateFilter): String? {
        val days = filter.days ?: return null
        val millis = System.currentTimeMillis() - days.toLong() * 24 * 60 * 60 * 1000
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).format(Date(millis))
    }
}
