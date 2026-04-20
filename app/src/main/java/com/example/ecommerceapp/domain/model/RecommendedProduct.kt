package com.example.ecommerceapp.domain.model

data class RecommendedProduct(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val image: String,
    val category: String
)
