package com.example.doan3

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun ProductDetailScreen(
    product: Product,
    currentUsername: String = "",
    onBack: () -> Unit,
    onAddToCart: (Product, Int) -> Unit = { _, _ -> }
) {
    var quantity by remember { mutableIntStateOf(1) }
    var isFavorite by remember { mutableStateOf(false) }
    var addedToCart by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Surface(shadowElevation = 12.dp, color = Color.White) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .shadow(4.dp, RoundedCornerShape(14.dp))
                            .background(Color.White, RoundedCornerShape(14.dp))
                            .clickable { isFavorite = !isFavorite },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isFavorite) AccentRed else Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Button(
                        onClick = {
                            if (product.stock >= quantity) {
                                onAddToCart(product, quantity)
                                addedToCart = true
                            }
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        enabled = product.stock > 0 && product.stock >= quantity,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (product.stock >= quantity) PrimaryBlack else Color.Gray
                        )
                    ) {
                        Icon(Icons.Outlined.ShoppingCart, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            when {
                                product.stock == 0 -> "Hết hàng"
                                product.stock < quantity -> "Không đủ hàng"
                                addedToCart -> "Đã thêm ✓"
                                else -> "Thêm vào giỏ"
                            },
                            fontSize = 15.sp, fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(4.dp, CircleShape)
                        .background(Color.White, CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(20.dp))
                }
                Text(
                    "CHI TIẾT SẢN PHẨM",
                    fontWeight = FontWeight.Bold, fontSize = 15.sp,
                    modifier = Modifier.weight(1f), textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(38.dp))
            }

            // Image Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceGray),
                contentAlignment = Alignment.Center
            ) {
                if (product.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.imageUrl).crossfade(true).build(),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Box(
                    modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
                        .clip(RoundedCornerShape(8.dp)).background(PrimaryBlack)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(product.category, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(product.name, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = PrimaryBlack)
                
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(product.price, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = PrimaryBlack)
                        // Hiển thị số lượng còn lại
                        Text(
                            text = if (product.stock > 0) "Số lượng còn lại: ${product.stock} sản phẩm" else "Tình trạng: Hết hàng",
                            fontSize = 14.sp,
                            color = if (product.stock > 0) Color.Gray else AccentRed,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val productReviews = reviewList.filter { it.productFirestoreId == product.firestoreId }
                        val avgStars = if (productReviews.isEmpty()) 0f 
                                       else productReviews.sumOf { it.stars }.toFloat() / productReviews.size
                        Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("${"%.1f".format(avgStars)} (${productReviews.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Quantity selector area
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Số lượng mua", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    if (product.stock > 0) {
                        Text("Sẵn có: ${product.stock}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceGray)
                        .padding(4.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        modifier = Modifier.size(36.dp).background(Color.White, CircleShape)
                    ) {
                        Icon(Icons.Filled.Remove, null, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = quantity.toString(),
                        modifier = Modifier.padding(horizontal = 24.dp),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                    IconButton(
                        onClick = { if (quantity < product.stock) quantity++ },
                        modifier = Modifier.size(36.dp).background(Color.White, CircleShape)
                    ) {
                        Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0))
                Spacer(modifier = Modifier.height(20.dp))

                Text("Mô tả sản phẩm", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = product.description.ifBlank { "Sản phẩm chất lượng cao, được lựa chọn kỹ càng để đảm bảo độ tươi ngon và an toàn thực phẩm." },
                    fontSize = 14.sp, color = Color(0xFF666666), lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(30.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0))
                Spacer(modifier = Modifier.height(20.dp))

                ReviewSection(product = product, currentUsername = currentUsername)
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun ReviewSection(product: Product, currentUsername: String) {
    val productReviews = reviewList.filter { it.productFirestoreId == product.firestoreId }
    val avgStars = if (productReviews.isEmpty()) 0f 
                   else productReviews.sumOf { it.stars }.toFloat() / productReviews.size

    var showWriteReview by remember { mutableStateOf(false) }
    var newStars by remember { mutableIntStateOf(5) }
    var newComment by remember { mutableStateOf("") }
    val alreadyReviewed = productReviews.any { it.username == currentUsername }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Đánh giá sản phẩm", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.weight(1f))
            if (productReviews.isNotEmpty()) {
                Text("${"%.1f".format(avgStars)}/5.0", fontWeight = FontWeight.Bold, color = Color(0xFFFFC107))
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))

        // PHẦN VIẾT ĐÁNH GIÁ
        if (currentUsername.isBlank()) {
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                .background(SurfaceGray).padding(16.dp), contentAlignment = Alignment.Center) {
                Text("Đăng nhập để để lại đánh giá", fontSize = 13.sp, color = Color.Gray)
            }
        } else if (alreadyReviewed) {
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE8F5E9)).padding(12.dp)) {
                Text("Bạn đã gửi đánh giá cho sản phẩm này ✓", fontSize = 13.sp, color = Color(0xFF2E7D32))
            }
        } else {
            if (!showWriteReview) {
                OutlinedButton(
                    onClick = { showWriteReview = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Edit, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Viết đánh giá của bạn")
                }
            } else {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Trải nghiệm của bạn", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Row(modifier = Modifier.padding(vertical = 8.dp)) {
                            repeat(5) { i ->
                                Icon(
                                    imageVector = if (i < newStars) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = null,
                                    tint = if (i < newStars) Color(0xFFFFC107) else Color.Gray,
                                    modifier = Modifier.size(32.dp).clickable { newStars = i + 1 }
                                )
                            }
                        }
                        OutlinedTextField(
                            value = newComment,
                            onValueChange = { newComment = it },
                            placeholder = { Text("Nhập nhận xét tại đây...", color = Color.LightGray) },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextButton(onClick = { showWriteReview = false }, modifier = Modifier.weight(1f)) {
                                Text("Hủy")
                            }
                            Button(
                                onClick = {
                                    com.example.doan3.firebase.FirebaseManager.saveReview(
                                        Review(
                                            id = nextReviewId(),
                                            productFirestoreId = product.firestoreId,
                                            username = currentUsername,
                                            stars = newStars,
                                            comment = newComment.trim()
                                        )
                                    )
                                    showWriteReview = false
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Gửi")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // DANH SÁCH ĐÁNH GIÁ
        if (productReviews.isEmpty()) {
            Text("Chưa có đánh giá nào cho sản phẩm này.", color = Color.Gray, fontSize = 14.sp)
        } else {
            productReviews.reversed().forEach { review ->
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFEEEEEE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(review.username.take(1).uppercase(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(review.username, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Row {
                                repeat(review.stars) {
                                    Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }
                    if (review.comment.isNotBlank()) {
                        Text(
                            text = review.comment,
                            fontSize = 13.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(top = 4.dp, start = 42.dp)
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 12.dp), color = Color(0xFFF9F9F9))
                }
            }
        }
    }
}
