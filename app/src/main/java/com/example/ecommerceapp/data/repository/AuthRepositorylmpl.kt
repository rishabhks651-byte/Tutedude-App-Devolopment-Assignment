package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.domain.model.AppUser
import com.example.ecommerceapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override val currentUser: Flow<AppUser?> = callbackFlow {
        val current = auth.currentUser
        if (current == null) {
            trySend(null)
        } else {
            firestore.collection(USERS_COLLECTION)
                .document(current.uid)
                .addSnapshotListener { snapshot, _ ->
                    val user = snapshot?.let {
                        AppUser(
                            id = current.uid,
                            name = it.getString("name").orEmpty(),
                            email = it.getString("email").orEmpty(),
                            contact = it.getString("contact").orEmpty()
                        )
                    } ?: AppUser(
                        id = current.uid,
                        name = current.displayName.orEmpty(),
                        email = current.email.orEmpty(),
                        contact = ""
                    )
                    trySend(user)
                }
        }

        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                trySend(null)
            } else {
                firestore.collection(USERS_COLLECTION)
                    .document(user.uid)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        trySend(
                            AppUser(
                                id = user.uid,
                                name = snapshot.getString("name").orEmpty(),
                                email = snapshot.getString("email").orEmpty().ifBlank { user.email.orEmpty() },
                                contact = snapshot.getString("contact").orEmpty()
                            )
                        )
                    }
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun register(
        name: String,
        email: String,
        contact: String,
        password: String
    ): Result<Unit> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: error("User was created without an id.")
        val profile = mapOf(
            "name" to name,
            "email" to email,
            "contact" to contact
        )
        firestore.collection(USERS_COLLECTION).document(uid).set(profile).await()
    }

    override fun logout() {
        auth.signOut()
    }

    private companion object {
        const val USERS_COLLECTION = "users"
    }
}
