package com.example.ecommerceapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.update { it.copy(currentUser = user) }
            }
        }
    }

    fun updateName(value: String) = _uiState.update { it.copy(name = value, errorMessage = null) }
    fun updateContact(value: String) = _uiState.update { it.copy(contact = value, errorMessage = null) }
    fun updateEmail(value: String) = _uiState.update { it.copy(email = value, errorMessage = null) }
    fun updatePassword(value: String) = _uiState.update { it.copy(password = value, errorMessage = null) }

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Enter a valid email and a 6+ character password.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.login(state.email.trim(), state.password)
                .onSuccess {
                    _uiState.update { current ->
                        current.copy(isLoading = false, password = "", errorMessage = null)
                    }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun register(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.name.isBlank() || state.contact.isBlank() || state.email.isBlank() || state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Complete all fields before creating your account.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.register(
                name = state.name.trim(),
                email = state.email.trim(),
                contact = state.contact.trim(),
                password = state.password
            ).onSuccess {
                _uiState.update {
                    AuthUiState(currentUser = it.currentUser)
                }
                onSuccess()
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.update { AuthUiState() }
    }
}
