package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.data.local.dao.FavoriteDao
import com.example.ecommerceapp.data.mapper.toDomain
import com.example.ecommerceapp.data.mapper.toEntity
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoritesRepository {
    override fun observeFavorites(): Flow<List<Product>> =
        favoriteDao.observeFavorites().map { entities -> entities.map { it.toDomain() } }

    override fun isFavorite(productId: String): Flow<Boolean> = favoriteDao.isFavorite(productId)

    override suspend fun addFavorite(product: Product) {
        favoriteDao.insertFavorite(product.toEntity())
    }

    override suspend fun removeFavorite(productId: String) {
        favoriteDao.deleteFavorite(productId)
    }
}
