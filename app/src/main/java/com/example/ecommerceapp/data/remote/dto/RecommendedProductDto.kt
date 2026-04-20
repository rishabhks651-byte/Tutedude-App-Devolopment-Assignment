package com.example.ecommerceapp.data.remote.dto

import com.example.ecommerceapp.domain.model.RecommendedProduct

data class RecommendedProductDto(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String
) {
    fun toDomain(): RecommendedProduct = RecommendedProduct(
        id = id.toString(),
        title = title,
        description = description,
        price = price,
        image = image,
        category = category
    )
}
