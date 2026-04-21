package com.example.ecommerceapp.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.domain.repository.FavoritesRepository
import com.example.ecommerceapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val productId: String = checkNotNull(savedStateHandle["productId"])

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    init {
        observeProduct()
        observeFavorite()
    }

    private fun observeProduct() {
        viewModelScope.launch {
            productRepository.observeProduct(productId).collect { product ->
                _uiState.update {
                    it.copy(
                        product = product,
                        isLoading = false,
                        errorMessage = if (product == null) "Product not found." else null
                    )
                }
            }
        }
    }

    private fun observeFavorite() {
        viewModelScope.launch {
            favoritesRepository.isFavorite(productId).collect { favorite ->
                _uiState.update { it.copy(isFavorite = favorite) }
            }
        }
    }

    fun toggleFavorite() {
        val product = _uiState.value.product ?: return
        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                favoritesRepository.removeFavorite(product.id)
            } else {
                favoritesRepository.addFavorite(product)
            }
        }
    }
}
