package com.pettrack.app.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pettrack.app.core.common.authErrorMessage
import com.pettrack.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val fullName: String = "",
    val nationalId: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun onFullName(v: String) = _state.update { it.copy(fullName = v, error = null) }
    fun onNationalId(v: String) = _state.update { it.copy(nationalId = v, error = null) }
    fun onPhone(v: String) = _state.update { it.copy(phone = v, error = null) }
    fun onEmail(v: String) = _state.update { it.copy(email = v, error = null) }
    fun onAddress(v: String) = _state.update { it.copy(address = v, error = null) }
    fun onPassword(v: String) = _state.update { it.copy(password = v, error = null) }

    fun submit() {
        val s = _state.value
        val problem = when {
            s.fullName.isBlank() -> "Ingresa tu nombre completo."
            s.email.isBlank() -> "Ingresa tu correo."
            s.password.length < 6 -> "La contraseña debe tener al menos 6 caracteres."
            else -> null
        }
        if (problem != null) {
            _state.update { it.copy(error = problem) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            repository.register(
                email = s.email,
                password = s.password,
                fullName = s.fullName,
                nationalId = s.nationalId,
                phone = s.phone,
                address = s.address,
            )
                .onSuccess { _state.update { it.copy(loading = false, success = true) } }
                .onFailure { e -> _state.update { it.copy(loading = false, error = authErrorMessage(e)) } }
        }
    }
}
