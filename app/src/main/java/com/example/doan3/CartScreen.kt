package com.example.doan3

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest

data class CartItem(val product: Product, val size: String, val quantity: Int)

fun parsePrice(price: String): Long =
    price.replace(".", "").replace("đ", "").replace(",", "").trim().toLongOrNull() ?: 0L

fun formatPrice(amount: Long): String {
    val s = amount.toString()
    val sb = StringBuilder()
    s.reversed().forEachIndexed { i, c -> if (i > 0 && i % 3 == 0) sb.append('.'); sb.append(c) }
    return sb.reverse().toString() + "đ"
}

// CART SCREEN
@Composable
fun CartScreen(
    cartItems: MutableList<CartItem>,
    username: String = "khách",
    isLoggedIn: Boolean = false,
    onBack: () -> Unit,
    onRequireLogin: () -> Unit = {}
) {
    var address     by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var showLogin   by remember { mutableStateOf(false) }
    var showAddressWarning by remember { mutableStateOf(false) }
    
    // Tính tổng tiền
    val total = cartItems.sumOf { parsePrice(it.product.price) * it.quantity }

    val currentUser = userAccounts.find { it.username == username }
    val currentUserId = currentUser?.firestoreId ?: ""

    if (showSuccess) {
        OrderSuccessDialog { 
            cartItems.clear()
            showSuccess = false
            onBack() 
        }
    }

    if (showLogin) {
        AlertDialog(onDismissRequest = { showLogin = false }, containerColor = Color.White,
            title = { Text("Yêu cầu đăng nhập", fontWeight = FontWeight.Bold) },
            text  = { Text("Bạn cần đăng nhập để đặt hàng.") },
            confirmButton = { Button(onClick = { showLogin = false; onRequireLogin() },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack),
                shape = RoundedCornerShape(10.dp)) { Text("Đăng nhập ngay") } },
            dismissButton = { TextButton(onClick = { showLogin = false }) { Text("Để sau") } })
    }

    if (showAddressWarning) {
        AlertDialog(onDismissRequest = { showAddressWarning = false }, containerColor = Color.White,
            title = { Text("Thiếu thông tin", fontWeight = FontWeight.Bold) },
            text  = { Text("Vui lòng nhập địa chỉ nhận hàng trước khi đặt hàng.") },
            confirmButton = { 
                Button(onClick = { showAddressWarning = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack),
                    shape = RoundedCornerShape(10.dp)) { Text("Đã hiểu") } 
            }
        )
    }

    Scaffold(containerColor = SurfaceGray,
        bottomBar = {
            Surface(shadowElevation = 12.dp, color = Color.White) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                    // Ô nhập địa chỉ
                    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .border(1.5.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                        .background(Color.White).padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.Top) {
                        Icon(Icons.Outlined.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(20.dp).padding(top = 2.dp))
                        Spacer(Modifier.width(8.dp))
                        BasicTextField(value = address, onValueChange = { address = it },
                            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 48.dp),
                            textStyle = TextStyle(fontSize = 14.sp, color = PrimaryBlack),
                            cursorBrush = SolidColor(PrimaryBlack),
                            decorationBox = { inner ->
                                if (address.isEmpty()) Text("Nhập địa chỉ nhận hàng...", color = Color.LightGray, fontSize = 14.sp)
                                inner()
                            })
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Tổng cộng", fontSize = 12.sp, color = Color.Gray)
                            Text(formatPrice(total), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = PrimaryBlack)
                        }
                        Button(onClick = {
                            if (cartItems.isEmpty()) return@Button
                            if (!isLoggedIn) { showLogin = true; return@Button }
                            if (address.isBlank()) { showAddressWarning = true; return@Button }

                            com.example.doan3.firebase.FirebaseManager.placeOrder(
                                order = Order(id = nextOrderId(), username = username, items = cartItems.toList(), address = address, total = total),
                                userId = currentUserId
                            )
                            showSuccess = true
                        }, modifier = Modifier.height(50.dp).widthIn(min = 140.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)) {
                            Text("Đặt hàng", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }) { pad ->
        Column(modifier = Modifier.padding(pad).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(38.dp).shadow(4.dp, CircleShape).background(Color.White, CircleShape).clickable { onBack() }, contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(20.dp))
                }
                Text("GIỎ HÀNG", fontWeight = FontWeight.Bold, fontSize = 17.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Spacer(Modifier.size(38.dp))
            }
            HorizontalDivider(color = Color(0xFFF0F0F0))
            if (cartItems.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🛒", fontSize = 48.sp); Spacer(Modifier.height(12.dp))
                        Text("Giỏ hàng trống", color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text("Hãy thêm sản phẩm yêu thích!", color = Color.LightGray, fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                    items(cartItems, key = { "${it.product.id}_${it.size}" }) { item ->
                        CartItemRow(
                            item = item, 
                            onIncrease = {
                                val idx = cartItems.indexOf(item)
                                if (idx != -1) {
                                    // Thay thế phần tử cũ bằng phần tử mới để kích hoạt cập nhật UI tức thì
                                    cartItems[idx] = item.copy(quantity = item.quantity + 1)
                                }
                            }, 
                            onDecrease = {
                                val idx = cartItems.indexOf(item)
                                if (idx != -1) {
                                    if (item.quantity > 1) {
                                        cartItems[idx] = item.copy(quantity = item.quantity - 1)
                                    } else {
                                        cartItems.removeAt(idx)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

// Thông báo đặt hàng
@Composable
fun OrderSuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, containerColor = Color.White,
        icon = { Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(48.dp)) },
        title = { Text("Đặt hàng thành công!", fontWeight = FontWeight.Bold) },
        text = { Text("Đơn hàng của bạn đang được xử lý. Cảm ơn bạn đã đặt hàng") },
        confirmButton = { Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack), shape = RoundedCornerShape(10.dp)) { Text("Tuyệt vời") } })
}

// Giao diện từng dòng sản phẩm
@Composable
fun CartItemRow(item: CartItem, onIncrease: () -> Unit, onDecrease: () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(item.product.imageUrl).crossfade(true).build(),
                contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)).background(SurfaceGray))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(item.product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(item.product.price, fontWeight = FontWeight.ExtraBold, color = PrimaryBlack, fontSize = 14.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(SurfaceGray).padding(horizontal = 4.dp, vertical = 2.dp)) {
                IconButton(onClick = onDecrease, modifier = Modifier.size(28.dp)) { Icon(Icons.Filled.Remove, null, modifier = Modifier.size(16.dp)) }
                Text(item.quantity.toString(), fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 8.dp))
                IconButton(onClick = onIncrease, modifier = Modifier.size(28.dp)) { Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp)) }
            }
        }
    }
}
