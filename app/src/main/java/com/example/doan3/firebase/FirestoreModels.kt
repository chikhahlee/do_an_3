package com.example.doan3.firebase

object FirestoreCollections {
    const val PRODUCTS = "products"
    const val USERS    = "users"
    const val ORDERS   = "orders"
    const val REVIEWS  = "reviews"
}

// ── products ─────────────────────────────────────────────
data class ProductModel(
    val category: String  = "",
    val createdAt: Double = 0.0,
    val id: String        = "",
    val imageUrl: String  = "",
    val name: String      = "",
    val price: String     = "",
    val stock: Int        = 0,
    val description: String = ""
)

// ── users ────────────────────────────────────────────────
data class UserModel(
    val createdAt: Double = 0.0,
    val email: String     = "",
    val id: String        = "",
    val password: String  = "",
    val phone: String     = "",
    val role: String      = "user",
    val username: String  = ""
)

// ── orders ───────────────────────────────────────────────
data class OrderModel(
    val address: String         = "",
    val createAt: Double        = 0.0,
    val id: String              = "",
    val items: List<OrderItem>  = emptyList(),
    val status: String          = "Chờ xác nhận",
    val total: Long             = 0L,
    val userId: String          = ""
)

// ── order items ──────────────────────────────────────────
data class OrderItem(
    val price: String       = "",
    val productId: String   = "",
    val productName: String = "",
    val quantity: Int       = 1
)

// ── reviews ──────────────────────────────────────────────
data class ReviewModel(
    val comment: String            = "",
    val createdAt: Double          = 0.0,
    val productFirestoreId: String = "",
    val stars: Int                 = 5,
    val username: String           = ""
)
