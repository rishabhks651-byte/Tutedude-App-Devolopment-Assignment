package com.example.ecommerceapp.presentation.upload

import android.net.Uri

data class UploadProductUiState(
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val selectedImages: List<Uri> = emptyList(),
    val isUploading: Boolean = false,
    val errorMessage: String? = null
)
