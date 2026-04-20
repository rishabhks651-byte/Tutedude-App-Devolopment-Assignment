package com.example.ecommerceapp.presentation.auth

import com.example.ecommerceapp.domain.model.AppUser

data class AuthUiState(
    val name: String = "",
    val contact: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentUser: AppUser? = null
)
