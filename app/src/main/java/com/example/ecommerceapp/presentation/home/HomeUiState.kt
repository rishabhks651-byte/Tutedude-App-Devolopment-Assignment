package com.example.ecommerceapp.presentation.home

import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.domain.model.RecommendedProduct

data class HomeUiState(
    val products: List<Product> = emptyList(),
    val recommendedProducts: List<RecommendedProduct> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
