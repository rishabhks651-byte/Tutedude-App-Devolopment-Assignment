package com.example.ecommerceapp.presentation.detail

import com.example.ecommerceapp.domain.model.Product

data class ProductDetailUiState(
    val product: Product? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
