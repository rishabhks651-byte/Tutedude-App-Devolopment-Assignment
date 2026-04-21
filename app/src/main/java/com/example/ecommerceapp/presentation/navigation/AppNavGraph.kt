package com.example.ecommerceapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ecommerceapp.presentation.auth.AuthViewModel
import com.example.ecommerceapp.presentation.auth.LoginScreen
import com.example.ecommerceapp.presentation.auth.RegisterScreen
import com.example.ecommerceapp.presentation.detail.ProductDetailScreen
import com.example.ecommerceapp.presentation.detail.ProductDetailViewModel
import com.example.ecommerceapp.presentation.favorites.FavoritesScreen
import com.example.ecommerceapp.presentation.favorites.FavoritesViewModel
import com.example.ecommerceapp.presentation.home.HomeScreen
import com.example.ecommerceapp.presentation.home.HomeViewModel
import com.example.ecommerceapp.presentation.upload.UploadProductScreen
import com.example.ecommerceapp.presentation.upload.UploadProductViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val startDestination = if (authState.currentUser == null) Routes.Login.route else Routes.Home.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.Login.route) {
            LoginScreen(
                state = authState,
                onEmailChange = authViewModel::updateEmail,
                onPasswordChange = authViewModel::updatePassword,
                onLogin = {
                    authViewModel.login(
                        onSuccess = {
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.Login.route) { inclusive = true }
                            }
                        }
                    )
                },
                onNavigateToRegister = { navController.navigate(Routes.Register.route) }
            )
        }

        composable(Routes.Register.route) {
            RegisterScreen(
                state = authState,
                onNameChange = authViewModel::updateName,
                onContactChange = authViewModel::updateContact,
                onEmailChange = authViewModel::updateEmail,
                onPasswordChange = authViewModel::updatePassword,
                onRegister = {
                    authViewModel.register(
                        onSuccess = {
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.Login.route) { inclusive = true }
                            }
                        }
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            HomeScreen(
                state = state,
                onProductClick = { navController.navigate(Routes.ProductDetail.createRoute(it)) },
                onUploadClick = { navController.navigate(Routes.Upload.route) },
                onFavoritesClick = { navController.navigate(Routes.Favorites.route) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onRetry = viewModel::refreshRecommended
            )
        }

        composable(
            route = Routes.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) {
            val viewModel: ProductDetailViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            ProductDetailScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onToggleFavorite = viewModel::toggleFavorite
            )
        }

        composable(Routes.Upload.route) {
            val viewModel: UploadProductViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            UploadProductScreen(
                state = state,
                onTitleChange = viewModel::updateTitle,
                onDescriptionChange = viewModel::updateDescription,
                onPriceChange = viewModel::updatePrice,
                onImagesSelected = viewModel::updateSelectedImages,
                onUpload = {
                    viewModel.upload(
                        onSuccess = { navController.popBackStack() }
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Favorites.route) {
            val viewModel: FavoritesViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            FavoritesScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onProductClick = { navController.navigate(Routes.ProductDetail.createRoute(it)) }
            )
        }
    }
}
