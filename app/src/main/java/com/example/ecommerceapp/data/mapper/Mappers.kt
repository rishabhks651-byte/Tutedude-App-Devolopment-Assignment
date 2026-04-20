package com.example.ecommerceapp.data.mapper

import com.example.ecommerceapp.data.local.entity.FavoriteEntity
import com.example.ecommerceapp.domain.model.Product

fun Product.toEntity(): FavoriteEntity = FavoriteEntity(
    id = id,
    title = title,
    description = description,
    price = price,
    imageUrl = imageUrls.firstOrNull().orEmpty(),
    uploaderId = uploaderId,
    uploaderName = uploaderName,
    uploaderContact = uploaderContact,
    createdAt = createdAt
)

fun FavoriteEntity.toDomain(): Product = Product(
    id = id,
    title = title,
    description = description,
    price = price,
    imageUrls = listOf(imageUrl).filter { it.isNotBlank() },
    uploaderId = uploaderId,
    uploaderName = uploaderName,
    uploaderContact = uploaderContact,
    createdAt = createdAt
)
