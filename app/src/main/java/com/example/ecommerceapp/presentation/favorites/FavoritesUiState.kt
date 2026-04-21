package com.example.ecommerceapp.presentation.favorites

import com.example.ecommerceapp.domain.model.Product

data class FavoritesUiState(
    val favorites: List<Product> = emptyList()
)
