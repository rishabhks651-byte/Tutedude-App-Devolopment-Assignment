package com.example.ecommerceapp.domain.repository

import com.example.ecommerceapp.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeFavorites(): Flow<List<Product>>
    fun isFavorite(productId: String): Flow<Boolean>
    suspend fun addFavorite(product: Product)
    suspend fun removeFavorite(productId: String)
}
