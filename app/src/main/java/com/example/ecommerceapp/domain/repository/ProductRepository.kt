package com.example.ecommerceapp.domain.repository

import android.net.Uri
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.domain.model.RecommendedProduct
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun observeProducts(): Flow<List<Product>>
    fun observeProduct(productId: String): Flow<Product?>
    suspend fun uploadProduct(
        title: String,
        description: String,
        price: Double,
        imageUris: List<Uri>
    ): Result<Unit>
    suspend fun fetchRecommendedProducts(): Result<List<RecommendedProduct>>
}
