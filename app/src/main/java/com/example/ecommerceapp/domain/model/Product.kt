package com.example.ecommerceapp.domain.model

data class Product(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrls: List<String> = emptyList(),
    val uploaderId: String = "",
    val uploaderName: String = "",
    val uploaderContact: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
