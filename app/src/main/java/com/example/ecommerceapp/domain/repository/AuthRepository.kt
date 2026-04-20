package com.example.ecommerceapp.domain.repository

import com.example.ecommerceapp.domain.model.AppUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<AppUser?>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(name: String, email: String, contact: String, password: String): Result<Unit>
    fun logout()
}
