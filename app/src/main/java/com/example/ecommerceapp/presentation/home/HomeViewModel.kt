package com.example.ecommerceapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeProducts()
        refreshRecommended()
    }

    private fun observeProducts() {
        viewModelScope.launch {
            productRepository.observeProducts().collect { products ->
                _uiState.update {
                    it.copy(
                        products = products,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun refreshRecommended() {
        viewModelScope.launch {
            productRepository.fetchRecommendedProducts()
                .onSuccess { items ->
                    _uiState.update { it.copy(recommendedProducts = items, errorMessage = null) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message, isLoading = false) }
                }
        }
    }
}
