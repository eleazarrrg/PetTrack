package com.pettrack.app.ui.pets.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pettrack.app.core.common.authErrorMessage
import com.pettrack.app.data.repository.PetRepository
import com.pettrack.app.domain.model.Pet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyReportsUiState(
    val loading: Boolean = true,
    val pets: List<Pet> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class MyReportsViewModel @Inject constructor(
    private val repository: PetRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MyReportsUiState())
    val state: StateFlow<MyReportsUiState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            repository.myPets()
                .onSuccess { pets -> _state.update { it.copy(loading = false, pets = pets) } }
                .onFailure { e -> _state.update { it.copy(loading = false, error = authErrorMessage(e)) } }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            repository.deletePet(id).onSuccess { load() }
        }
    }
}
