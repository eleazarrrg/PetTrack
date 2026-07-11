package com.pettrack.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pettrack.app.core.common.authErrorMessage
import com.pettrack.app.data.repository.AuthRepository
import com.pettrack.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val loading: Boolean = true,
    val fullName: String = "",
    val nationalId: String = "",
    val phone: String = "",
    val address: String = "",
    val email: String? = null,
    val saving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
    val loggedOut: Boolean = false,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepo: ProfileRepository,
    private val authRepo: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState(email = profileRepo.email))
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init { load() }

    fun onFullName(v: String) = _state.update { it.copy(fullName = v, saved = false) }
    fun onNationalId(v: String) = _state.update { it.copy(nationalId = v, saved = false) }
    fun onPhone(v: String) = _state.update { it.copy(phone = v, saved = false) }
    fun onAddress(v: String) = _state.update { it.copy(address = v, saved = false) }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            profileRepo.myProfile()
                .onSuccess { p ->
                    _state.update {
                        it.copy(
                            loading = false,
                            fullName = p.fullName.orEmpty(),
                            nationalId = p.nationalId.orEmpty(),
                            phone = p.phone.orEmpty(),
                            address = p.address.orEmpty(),
                            email = p.email ?: it.email,
                        )
                    }
                }
                .onFailure { e -> _state.update { it.copy(loading = false, error = authErrorMessage(e)) } }
        }
    }

    fun save() {
        val s = _state.value
        viewModelScope.launch {
            _state.update { it.copy(saving = true, error = null, saved = false) }
            profileRepo.update(s.fullName, s.nationalId, s.phone, s.address)
                .onSuccess { _state.update { it.copy(saving = false, saved = true) } }
                .onFailure { e -> _state.update { it.copy(saving = false, error = authErrorMessage(e)) } }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepo.logout()
            _state.update { it.copy(loggedOut = true) }
        }
    }
}
