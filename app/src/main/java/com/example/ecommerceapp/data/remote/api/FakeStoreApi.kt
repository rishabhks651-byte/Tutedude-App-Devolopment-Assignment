package com.example.ecommerceapp.data.remote.api

import com.example.ecommerceapp.data.remote.dto.RecommendedProductDto
import retrofit2.http.GET

interface FakeStoreApi {
    @GET("products")
    suspend fun getProducts(): List<RecommendedProductDto>
}
