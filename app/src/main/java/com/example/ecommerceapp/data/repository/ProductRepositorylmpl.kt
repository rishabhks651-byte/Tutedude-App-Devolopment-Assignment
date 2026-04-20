package com.example.ecommerceapp.data.repository

import android.net.Uri
import com.example.ecommerceapp.data.remote.api.FakeStoreApi
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.domain.model.RecommendedProduct
import com.example.ecommerceapp.domain.repository.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth,
    private val fakeStoreApi: FakeStoreApi
) : ProductRepository {

    override fun observeProducts(): Flow<List<Product>> = callbackFlow {
        val registration = firestore.collection(PRODUCTS_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val products = snapshot?.documents.orEmpty().map { document ->
                    document.toProduct()
                }.sortedByDescending { it.createdAt }
                trySend(products)
            }
        awaitClose { registration.remove() }
    }

    override fun observeProduct(productId: String): Flow<Product?> = callbackFlow {
        val registration = firestore.collection(PRODUCTS_COLLECTION)
            .document(productId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toProduct())
            }
        awaitClose { registration.remove() }
    }

    override suspend fun uploadProduct(
        title: String,
        description: String,
        price: Double,
        imageUris: List<Uri>
    ): Result<Unit> = runCatching {
        val user = auth.currentUser ?: error("You must be logged in to upload a product.")
        val userSnapshot = firestore.collection(USERS_COLLECTION).document(user.uid).get().await()
        val imageUrls = imageUris.mapIndexed { index, uri ->
            val path = "products/${user.uid}/${UUID.randomUUID()}_$index.jpg"
            val ref = storage.reference.child(path)
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        }
        val document = firestore.collection(PRODUCTS_COLLECTION).document()
        val payload = mapOf(
            "id" to document.id,
            "title" to title,
            "description" to description,
            "price" to price,
            "imageUrls" to imageUrls,
            "uploaderId" to user.uid,
            "uploaderName" to userSnapshot.getString("name").orEmpty().ifBlank { user.email.orEmpty() },
            "uploaderContact" to userSnapshot.getString("contact").orEmpty().ifBlank { user.email.orEmpty() },
            "createdAt" to System.currentTimeMillis()
        )
        document.set(payload).await()
    }

    override suspend fun fetchRecommendedProducts(): Result<List<RecommendedProduct>> = runCatching {
        fakeStoreApi.getProducts().map { it.toDomain() }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toProduct(): Product = Product(
        id = getString("id").orEmpty().ifBlank { id },
        title = getString("title").orEmpty(),
        description = getString("description").orEmpty(),
        price = getDouble("price") ?: 0.0,
        imageUrls = get("imageUrls") as? List<String> ?: emptyList(),
        uploaderId = getString("uploaderId").orEmpty(),
        uploaderName = getString("uploaderName").orEmpty(),
        uploaderContact = getString("uploaderContact").orEmpty(),
        createdAt = getLong("createdAt") ?: 0L
    )

    private companion object {
        const val PRODUCTS_COLLECTION = "products"
        const val USERS_COLLECTION = "users"
    }
}
