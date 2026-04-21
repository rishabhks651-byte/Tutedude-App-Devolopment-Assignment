package com.example.ecommerceapp.presentation.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class UploadProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UploadProductUiState())
    val uiState: StateFlow<UploadProductUiState> = _uiState.asStateFlow()

    fun updateTitle(value: String) = _uiState.update { it.copy(title = value, errorMessage = null) }
    fun updateDescription(value: String) = _uiState.update { it.copy(description = value, errorMessage = null) }
    fun updatePrice(value: String) = _uiState.update { it.copy(price = value, errorMessage = null) }
    fun updateSelectedImages(images: List<Uri>) = _uiState.update { it.copy(selectedImages = images, errorMessage = null) }

    fun upload(onSuccess: () -> Unit) {
        val state = _uiState.value
        val price = state.price.toDoubleOrNull()
        when {
            state.title.isBlank() -> _uiState.update { it.copy(errorMessage = "Title is required.") }
            state.description.isBlank() -> _uiState.update { it.copy(errorMessage = "Description is required.") }
            price == null || price <= 0.0 -> _uiState.update { it.copy(errorMessage = "Enter a valid price.") }
            state.selectedImages.size < 3 -> _uiState.update { it.copy(errorMessage = "Select at least 3 images.") }
            else -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isUploading = true, errorMessage = null) }
                    productRepository.uploadProduct(
                        title = state.title.trim(),
                        description = state.description.trim(),
                        price = price,
                        imageUris = state.selectedImages
                    ).onSuccess {
                        _uiState.update { UploadProductUiState() }
                        onSuccess()
                    }.onFailure { error ->
                        _uiState.update { it.copy(isUploading = false, errorMessage = error.message) }
                    }
                }
            }
        }
    }
}
