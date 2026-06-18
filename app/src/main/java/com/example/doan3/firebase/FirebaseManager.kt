package com.example.doan3.firebase

import androidx.compose.ui.graphics.Color
import com.example.doan3.CartItem
import com.example.doan3.Order
import com.example.doan3.Product
import com.example.doan3.Review
import com.example.doan3.UserAccount
import com.example.doan3.orderList
import com.example.doan3.productList
import com.example.doan3.reviewList
import com.example.doan3.userAccounts
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

object FirebaseManager {

    private val db get() = Firebase.firestore
    private val scope = CoroutineScope(Dispatchers.IO)

    fun init() {
        listenProducts()
        listenUsers()
        listenOrders()
        listenReviews()
    }

    // ---------------- PRODUCTS ----------------

    private fun listenProducts() {
        db.collection(FirestoreCollections.PRODUCTS).addSnapshotListener { snap, err ->
            if (err != null) return@addSnapshotListener
            snap ?: return@addSnapshotListener
            val list = snap.documents.mapNotNull { doc ->
                try {
                    Product(
                        id          = doc.id.hashCode(),
                        name        = doc.getString("name") ?: "",
                        price       = doc.getString("price") ?: "",
                        category    = doc.getString("category") ?: "",
                        color       = Color.Gray, 
                        firestoreId = doc.id,
                        imageUrl    = doc.getString("imageUrl") ?: "",
                        stock       = (doc.getLong("stock") ?: 0L).toInt(),
                        description = doc.getString("description") ?: ""
                    )
                } catch (_: Exception) { null }
            }
            productList.clear()
            productList.addAll(list)
        }
    }

    fun addProduct(product: Product, onDone: () -> Unit = {}) = scope.launch {
        val model = ProductModel(
            category = product.category,
            createdAt = System.currentTimeMillis().toDouble(),
            imageUrl = product.imageUrl,
            name = product.name,
            price = product.price,
            stock = product.stock,
            description = product.description
        )
        val ref = db.collection(FirestoreCollections.PRODUCTS).add(model).await()
        db.collection(FirestoreCollections.PRODUCTS).document(ref.id).update("id", ref.id).await()
        onDone()
    }

    fun updateProduct(product: Product, onDone: () -> Unit = {}) = scope.launch {
        val fsId = product.firestoreId
        if (fsId.isEmpty()) return@launch
        db.collection(FirestoreCollections.PRODUCTS).document(fsId).update(
            mapOf(
                "name" to product.name,
                "price" to product.price,
                "category" to product.category,
                "imageUrl" to product.imageUrl,
                "stock" to product.stock,
                "description" to product.description
            )
        ).await()
        onDone()
    }

    fun deleteProduct(product: Product, onDone: () -> Unit = {}) = scope.launch {
        if (product.firestoreId.isNotEmpty()) {
            db.collection(FirestoreCollections.PRODUCTS).document(product.firestoreId).delete().await()
        }
        onDone()
    }

    fun updateProductStock(firestoreId: String, stock: Int) = scope.launch {
        try {
            db.collection(FirestoreCollections.PRODUCTS).document(firestoreId).update("stock", stock).await()
        } catch (_: Exception) { }
    }

    fun decreaseStock(firestoreId: String, qty: Int) = scope.launch {
        try {
            val doc = db.collection(FirestoreCollections.PRODUCTS).document(firestoreId).get().await()
            val current = (doc.getLong("stock") ?: 0L).toInt()
            val newStock = maxOf(0, current - qty)
            db.collection(FirestoreCollections.PRODUCTS).document(firestoreId).update("stock", newStock).await()
        } catch (_: Exception) { }
    }

    fun increaseStock(firestoreId: String, qty: Int) = scope.launch {
        try {
            val doc = db.collection(FirestoreCollections.PRODUCTS).document(firestoreId).get().await()
            val current = (doc.getLong("stock") ?: 0L).toInt()
            val newStock = current + qty
            db.collection(FirestoreCollections.PRODUCTS).document(firestoreId).update("stock", newStock).await()
        } catch (_: Exception) { }
    }

    // ---------------- USERS ----------------

    private fun listenUsers() {
        db.collection(FirestoreCollections.USERS).addSnapshotListener { snap, err ->
            if (err != null) return@addSnapshotListener
            snap ?: return@addSnapshotListener
            val list = snap.documents.mapNotNull { doc ->
                try {
                    UserAccount(
                        id          = doc.id.hashCode(),
                        username    = doc.getString("username") ?: "",
                        email       = doc.getString("email") ?: "",
                        password    = "",
                        role        = doc.getString("role") ?: "user",
                        firestoreId = doc.id,
                        avatarUrl   = "" 
                    )
                } catch (_: Exception) { null }
            }
            userAccounts.clear()
            userAccounts.addAll(list)
        }
    }

    suspend fun login(username: String, password: String): UserAccount? {
        return try {
            withTimeout(10_000) {
                val snap = db.collection(FirestoreCollections.USERS)
                    .whereEqualTo("username", username).limit(1).get().await()
                val doc = snap.documents.firstOrNull() ?: return@withTimeout null
                if (doc.getString("password") != password) return@withTimeout null
                UserAccount(
                    id          = doc.id.hashCode(),
                    username    = doc.getString("username") ?: "",
                    email       = doc.getString("email") ?: "",
                    password    = password,
                    role        = doc.getString("role") ?: "user",
                    firestoreId = doc.id,
                    avatarUrl   = ""
                )
            }
        } catch (_: Exception) { null }
    }

    suspend fun register(username: String, email: String, phone: String, password: String): String? {
        return try {
            withTimeout(10_000) {
                val model = UserModel(
                    createdAt = System.currentTimeMillis().toDouble(),
                    email = email,
                    password = password,
                    phone = phone,
                    role = "user",
                    username = username
                )
                val ref = db.collection(FirestoreCollections.USERS).add(model).await()
                db.collection(FirestoreCollections.USERS).document(ref.id).update("id", ref.id).await()
                null
            }
        } catch (e: Exception) { e.message }
    }

    suspend fun changePassword(firestoreId: String, oldPassword: String, newPassword: String): Boolean {
        return try {
            val doc = db.collection(FirestoreCollections.USERS).document(firestoreId).get().await()
            val stored = doc.getString("password") ?: return false
            if (stored != oldPassword) return false
            db.collection(FirestoreCollections.USERS).document(firestoreId).update("password", newPassword).await()
            true
        } catch (_: Exception) { false }
    }

    fun updateUser(user: UserAccount, onDone: () -> Unit = {}) = scope.launch {
        val fsId = user.firestoreId
        if (fsId.isEmpty()) return@launch
        db.collection(FirestoreCollections.USERS).document(fsId).update(
            mapOf(
                "username" to user.username,
                "email" to user.email,
                "role" to user.role
            )
        ).await()
        onDone()
    }

    fun deleteUser(user: UserAccount, onDone: () -> Unit = {}) = scope.launch {
        if (user.firestoreId.isNotEmpty()) {
            db.collection(FirestoreCollections.USERS).document(user.firestoreId).delete().await()
        }
        onDone()
    }

    // ---------------- ORDERS ----------------

    private fun listenOrders() {
        db.collection(FirestoreCollections.ORDERS).addSnapshotListener { snap, err ->
            if (err != null) return@addSnapshotListener
            snap ?: return@addSnapshotListener
            val list = snap.documents.mapNotNull { doc ->
                try {
                    @Suppress("UNCHECKED_CAST")
                    val rawItems = doc.get("items") as? List<Map<String, Any>> ?: emptyList()
                    val items = rawItems.map { item ->
                        CartItem(
                            product = Product(
                                id = 0,
                                name = item["productName"] as? String ?: "",
                                price = item["price"] as? String ?: "",
                                category = "", 
                                color = Color.Gray, 
                                firestoreId = item["productId"] as? String ?: ""
                            ),
                            size = "", 
                            quantity = (item["quantity"] as? Long)?.toInt() ?: 1
                        )
                    }
                    Order(
                        id          = doc.id.hashCode(),
                        userId      = doc.getString("userId") ?: "", 
                        username    = doc.getString("username") ?: "",
                        items       = items,
                        address     = doc.getString("address") ?: "",
                        total       = doc.getLong("total") ?: 0L,
                        status      = doc.getString("status") ?: "Chờ xác nhận",
                        firestoreId = doc.id
                    )
                } catch (_: Exception) { null }
            }
            orderList.clear()
            orderList.addAll(list)
        }
    }

    fun placeOrder(order: Order, userId: String, onDone: () -> Unit = {}) = scope.launch {
        val model = OrderModel(
            address = order.address,
            createAt = System.currentTimeMillis().toDouble(),
            items = order.items.map { ci ->
                OrderItem(productId = ci.product.firestoreId, productName = ci.product.name,
                    price = ci.product.price, quantity = ci.quantity)
            },
            status = order.status,
            total = order.total,
            userId = userId
        )
        val ref = db.collection(FirestoreCollections.ORDERS).add(model).await()
        db.collection(FirestoreCollections.ORDERS).document(ref.id).update("id", ref.id).await()
        
        order.items.forEach { ci ->
            if (ci.product.firestoreId.isNotEmpty()) decreaseStock(ci.product.firestoreId, ci.quantity)
        }
        onDone()
    }

    // ---------------- REVIEWS ----------------

    fun saveReview(review: Review) = scope.launch {
        try {
            val model = ReviewModel(
                comment = review.comment,
                createdAt = review.createdAt.toDouble(),
                productFirestoreId = review.productFirestoreId,
                stars = review.stars,
                username = review.username
            )
            val ref = db.collection(FirestoreCollections.REVIEWS).add(model).await()
            db.collection(FirestoreCollections.REVIEWS).document(ref.id).update("id", ref.id).await()
        } catch (_: Exception) { }
    }

    fun listenReviews() {
        db.collection(FirestoreCollections.REVIEWS).addSnapshotListener { snap, _ ->
            snap ?: return@addSnapshotListener
            val list = snap.documents.mapNotNull { doc ->
                try {
                    Review(
                        id = doc.id.hashCode(),
                        productFirestoreId = doc.getString("productFirestoreId") ?: "",
                        username  = doc.getString("username") ?: "",
                        stars     = (doc.getLong("stars") ?: 5L).toInt(),
                        comment   = doc.getString("comment") ?: "",
                        createdAt = (doc.getDouble("createdAt") ?: 0.0).toLong()
                    )
                } catch (_: Exception) { null }
            }
            reviewList.clear()
            reviewList.addAll(list)
        }
    }
    
    fun updateOrderStatus(order: Order, status: String, onDone: () -> Unit = {}) = scope.launch {
        if (order.firestoreId.isNotEmpty()) {
            db.collection(FirestoreCollections.ORDERS).document(order.firestoreId).update("status", status).await()
            if (status == "Đã hủy" || status == "Trả hàng") {
                order.items.forEach { ci ->
                    if (ci.product.firestoreId.isNotEmpty()) increaseStock(ci.product.firestoreId, ci.quantity)
                }
            }
        }
        onDone()
    }
}
