package com.example.ecommerceapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ecommerceapp.presentation.components.ProductCard

@Composable
fun HomeScreen(
    state: HomeUiState,
    onProductClick: (String) -> Unit,
    onUploadClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onLogout: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Marketplace") },
                actions = {
                    TextButton(onClick = onFavoritesClick) { Text("Favorites") }
                    TextButton(onClick = onLogout) { Text("Logout") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "All products",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "Browse items uploaded by the community.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Button(onClick = onUploadClick) {
                        Text("Upload")
                    }
                }
            }

            if (state.errorMessage != null) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = state.errorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                            TextButton(onClick = onRetry) {
                                Text("Retry recommendations")
                            }
                        }
                    }
                }
            }

            if (state.recommendedProducts.isNotEmpty()) {
                item {
                    Text(
                        text = "Recommended",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                items(state.recommendedProducts) { product ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            AsyncImage(
                                model = product.image,
                                contentDescription = product.title,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = product.title,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                text = "$${product.price}",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Latest uploads",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(state.products) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product.id) }
                )
            }
        }
    }
}
