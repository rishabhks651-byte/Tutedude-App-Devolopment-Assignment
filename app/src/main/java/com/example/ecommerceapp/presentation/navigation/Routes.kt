package com.example.ecommerceapp.presentation.navigation

sealed class Routes(val route: String) {
    data object Login : Routes("login")
    data object Register : Routes("register")
    data object Home : Routes("home")
    data object Upload : Routes("upload")
    data object Favorites : Routes("favorites")
    data object ProductDetail : Routes("product_detail/{productId}") {
        fun createRoute(productId: String): String = "product_detail/$productId"
    }
}
