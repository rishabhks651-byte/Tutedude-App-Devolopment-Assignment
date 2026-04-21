# Tutedude-App-Devolopment-Assignment
Tutedude AppDev Assignment

E-Commerce Android App Assignment Guide
This document turns the assignment brief into a practical implementation plan for a Kotlin Android app using:

MVVM
Hilt
Firebase Authentication
Firebase Firestore
Firebase Storage
Room
Jetpack Compose
Optional: Retrofit + FakeStore API
Recommended Tech Stack
Language: Kotlin
UI: Jetpack Compose
Architecture: MVVM
Dependency Injection: Hilt
Local DB: Room
Auth: Firebase Authentication
Cloud DB: Firebase Firestore
Image Storage: Firebase Storage
Navigation: Navigation Compose
Async: Kotlin Coroutines + Flow
Image Loading: Coil
Optional API: Retrofit + Moshi or Gson
Suggested App Flow
User opens app.
If not logged in, show LoginScreen.
User can navigate to RegisterScreen.
After login, show HomeScreen.
Home screen lists uploaded products.
Clicking a product opens ProductDetailScreen.
User can favorite a product.
User can open UploadProductScreen and upload a new product with at least 3 images.
User can open FavoritesScreen to view locally stored favorite items.
Core Features Mapping
1. Authentication
Use Firebase Email/Password authentication:

Register with email + password
Login with email + password
Logout
Show simple error messages for invalid credentials, network failure, or empty fields
2. Home Screen
Display all products from Firestore:

Product image
Title
Short description
Price
Also optionally show a Recommended section from FakeStore API.

3. Product Details
Show:

Full description
Product images
Price
Seller/uploader name
Seller contact info
Favorite button
4. Upload Product
Allow user to add:

Title
Description
Price
3 or more images
Upload flow:

Upload images to Firebase Storage
Get image URLs
Save product data in Firestore
5. Favorites
Store favorites locally with Room:

Add product to favorites
Remove product from favorites
List favorites
Recommended Package Structure
com.example.ecommerceapp
|
|-- data
|   |-- local
|   |   |-- dao
|   |   |   `-- FavoriteDao.kt
|   |   |-- db
|   |   |   `-- AppDatabase.kt
|   |   `-- entity
|   |       `-- FavoriteEntity.kt
|   |
|   |-- remote
|   |   |-- api
|   |   |   `-- FakeStoreApi.kt
|   |   |-- dto
|   |   |   `-- RecommendedProductDto.kt
|   |   `-- firebase
|   |       |-- AuthDataSource.kt
|   |       |-- ProductRemoteDataSource.kt
|   |       `-- StorageDataSource.kt
|   |
|   `-- repository
|       |-- AuthRepositoryImpl.kt
|       |-- ProductRepositoryImpl.kt
|       `-- FavoritesRepositoryImpl.kt
|
|-- di
|   |-- AppModule.kt
|   |-- DatabaseModule.kt
|   `-- NetworkModule.kt
|
|-- domain
|   |-- model
|   |   |-- Product.kt
|   |   |-- User.kt
|   |   |-- FavoriteProduct.kt
|   |   `-- RecommendedProduct.kt
|   |
|   `-- repository
|       |-- AuthRepository.kt
|       |-- ProductRepository.kt
|       `-- FavoritesRepository.kt
|
|-- presentation
|   |-- auth
|   |   |-- LoginScreen.kt
|   |   |-- RegisterScreen.kt
|   |   `-- AuthViewModel.kt
|   |
|   |-- home
|   |   |-- HomeScreen.kt
|   |   `-- HomeViewModel.kt
|   |
|   |-- detail
|   |   |-- ProductDetailScreen.kt
|   |   `-- ProductDetailViewModel.kt
|   |
|   |-- upload
|   |   |-- UploadProductScreen.kt
|   |   `-- UploadProductViewModel.kt
|   |
|   |-- favorites
|   |   |-- FavoritesScreen.kt
|   |   `-- FavoritesViewModel.kt
|   |
|   `-- navigation
|       `-- AppNavGraph.kt
|
`-- MainActivity.kt
Domain Models
Product
data class Product(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrls: List<String> = emptyList(),
    val uploaderId: String = "",
    val uploaderName: String = "",
    val uploaderContact: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
FavoriteEntity
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val uploaderName: String,
    val uploaderContact: String
)
Firestore Structure
Recommended collection:

products
  |- productId
      |- title: String
      |- description: String
      |- price: Double
      |- imageUrls: List<String>
      |- uploaderId: String
      |- uploaderName: String
      |- uploaderContact: String
      |- createdAt: Timestamp/Long
Optional user profile collection:

users
  |- userId
      |- name: String
      |- email: String
      |- contact: String
Room Components
DAO
@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(product: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteFavorite(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    fun isFavorite(id: String): Flow<Boolean>
}
Database
@Database(entities = [FavoriteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
Repository Responsibilities
AuthRepository
Register user
Login user
Logout user
Get current user
ProductRepository
Fetch all products from Firestore
Fetch single product details
Upload product
Upload images to Firebase Storage
Optionally fetch recommended products from FakeStore
FavoritesRepository
Add favorite
Remove favorite
Observe favorites
Check if product is favorited
ViewModel Responsibilities
AuthViewModel
Hold login/register form state
Trigger Firebase auth methods
Expose loading, success, and error state
HomeViewModel
Fetch products from Firestore
Optionally fetch recommended products
Expose loading/error states
ProductDetailViewModel
Load product details
Observe favorite state
Add/remove favorite
UploadProductViewModel
Validate title/description/price/images
Upload images + product data
Expose upload progress and result
FavoritesViewModel
Observe local favorite list from Room
Remove items if needed
Navigation Routes
Use routes like:

login
register
home
product_detail/{productId}
upload
favorites
UI Suggestions
Use Material 3 Compose components:

Scaffold
TopAppBar
LazyColumn
Card
OutlinedTextField
Button
AsyncImage
NavigationBar or BottomAppBar
Screens To Build
LoginScreen
RegisterScreen
HomeScreen
ProductDetailScreen
UploadProductScreen
FavoritesScreen
Hilt Setup
Add:

@HiltAndroidApp on Application class
@AndroidEntryPoint on MainActivity
@HiltViewModel on all ViewModels
Provide dependencies through modules:

FirebaseAuth
FirebaseFirestore
FirebaseStorage
Room database
DAO
Retrofit instance
API interface
Validation Rules
Recommended validations:

Email must not be empty
Password must be at least 6 characters
Product title must not be empty
Description must not be empty
Price must be a valid positive number
At least 3 images must be selected before upload
Optional FakeStore API
Endpoint:

https://fakestoreapi.com/products
Example Retrofit interface:

interface FakeStoreApi {
    @GET("products")
    suspend fun getProducts(): List<RecommendedProductDto>
}
Show these items under a Recommended section in HomeScreen.

Suggested Dependencies
Add the equivalent latest stable versions in your Gradle files:

androidx.compose.ui
androidx.compose.material3
androidx.navigation:navigation-compose
androidx.lifecycle:lifecycle-viewmodel-compose
androidx.lifecycle:lifecycle-runtime-ktx
androidx.hilt:hilt-navigation-compose
com.google.dagger:hilt-android
com.google.dagger:hilt-compiler
androidx.room:room-runtime
androidx.room:room-ktx
androidx.room:room-compiler
com.google.firebase:firebase-auth-ktx
com.google.firebase:firebase-firestore-ktx
com.google.firebase:firebase-storage-ktx
io.coil-kt:coil-compose
com.squareup.retrofit2:retrofit
com.squareup.retrofit2:converter-moshi or converter-gson
Step-by-Step Build Order
Create Android project with Empty Compose Activity.
Add Hilt and configure dependency injection.
Add Firebase to the project and connect Authentication, Firestore, and Storage.
Implement login and registration screens.
Build Firestore product model and product listing on home screen.
Build product details screen.
Add Room database for favorites.
Implement upload product flow with Firebase Storage.
Add optional Retrofit-based recommended products section.
Improve UI consistency and error handling.
Submission Checklist
User can register
User can login
User can see all uploaded products
User can open product details
User can upload a product with at least 3 images
User can add/remove favorites
Favorites persist locally using Room
App uses MVVM
App uses Hilt
UI is clean and consistent
Best Choice For This Assignment
If you want the easiest clean implementation:

Use Jetpack Compose
Use Firestore
Use Firebase Storage
Use Room
Use Hilt
Keep product images as a simple list of download URLs
What I Recommend Next
The strongest next step is to scaffold the actual Android project with:

base Gradle setup
package structure
Hilt setup
Firebase placeholders
Room database
navigation
screen/viewmodel stubs

Starter Project Notes
The scaffold in this workspace already includes:

Compose navigation
Hilt modules
Room favorites storage
Firebase auth/product repository wiring
Upload screen with multi-image picker
Optional FakeStore Retrofit setup
Before the app will build and run in Android Studio, add:

google-services.json inside app/google-services.json
Firebase project services for Authentication, Firestore, and Storage
An Android Gradle wrapper if you want to build from the command line in this workspace
Suggested next implementation pass:

Add launcher icons and a proper app logo
Configure Firestore security rules and Storage rules
Improve product upload UX with image previews
Add loading and empty states across all screens
Add tests for ViewModels and repository mappers
