package com.example.doan3

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun AccountMainScreen(onBack: () -> Unit, onLogin: () -> Unit, onRegister: () -> Unit, showBack: Boolean = true) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại",
                    modifier = Modifier.size(24.dp).clickable { onBack() })
            } else {
                Spacer(modifier = Modifier.size(24.dp))
            }
            Text(
                text = "Thông tin tài khoản",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(24.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEEEEEE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Color(0xFFBBBBBB),
                    modifier = Modifier.size(60.dp)
                )
            }
            Spacer(modifier = Modifier.height(44.dp))
            OutlinedButton(
                onClick = onLogin,
                modifier = Modifier.width(220.dp).height(48.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
            ) {
                Text("Đăng nhập", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("hoặc", fontSize = 13.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onRegister,
                modifier = Modifier.width(220.dp).height(48.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
            ) {
                Text("Đăng ký", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
        Spacer(modifier = Modifier.weight(1.5f))
    }
}

@Composable
fun LoginScreen(onBack: () -> Unit, onGoRegister: () -> Unit = {}, onLoginSuccess: (String, String, String, Boolean) -> Unit = { _, _, _, _ -> }) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Quay lại",
                modifier = Modifier.size(24.dp).clickable { onBack() }
            )
            Text(
                text = "LOGIN",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(24.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Lock, null, tint = Color.DarkGray, modifier = Modifier.size(64.dp))
        }

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Tên đăng nhập") },
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = Color.Gray) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Mật khẩu") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color.Gray) },
            trailingIcon = {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = null,
                    modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                )
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (username.isBlank() || password.isBlank()) {
                    loginError = "Vui lòng nhập đầy đủ thông tin"
                    return@Button
                }
                isLoading = true
                loginError = ""
                scope.launch {
                    val found = com.example.doan3.firebase.FirebaseManager.login(username.trim(), password)
                    isLoading = false
                    if (found != null) {
                        onLoginSuccess(found.username, found.email, found.firestoreId, found.role == "admin")
                    } else {
                        loginError = "Tên đăng nhập hoặc mật khẩu không đúng"
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
            } else {
                Text("Đăng nhập", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (loginError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(loginError, color = Color.Red, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Text("Bạn chưa có tài khoản? ", fontSize = 13.sp, color = Color.Gray)
            Text("Đăng ký ngay", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onGoRegister() })
        }
    }
}

@RequiresApi(Build.VERSION_CODES.FROYO)
@Composable
fun RegisterScreen(onBack: () -> Unit, onGoLogin: () -> Unit = {}) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val isEmailValid = remember(email) {
        email.isEmpty() || android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { 
                showSuccess = false
                onBack() 
            },
            title = { Text("Thành công") },
            text = { Text("Đăng ký tài khoản thành công!") },
            confirmButton = { 
                TextButton(onClick = { 
                    showSuccess = false
                    onBack() 
                }) { Text("OK") } 
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState()).padding(24.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(24.dp).clickable { onBack() })
            Text("Đăng ký tài khoản", fontWeight = FontWeight.Bold, fontSize = 17.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(Modifier.width(24.dp))
        }

        OutlinedTextField(
            value = username, 
            onValueChange = { username = it }, 
            label = { Text("Tên đăng nhập") }, 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )
        
        Spacer(Modifier.height(12.dp))
        
        OutlinedTextField(
            value = email, 
            onValueChange = { email = it }, 
            label = { Text("Email") }, 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(10.dp),
            isError = !isEmailValid,
            supportingText = {
                if (!isEmailValid) {
                    Text("Email phải có '@' và đúng định dạng (vd: abc@gmail.com)", color = Color.Red)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )
        
        Spacer(Modifier.height(12.dp))
        
        OutlinedTextField(
            value = phone, 
            onValueChange = { phone = it }, 
            label = { Text("Số điện thoại") }, 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true
        )
        
        Spacer(Modifier.height(12.dp))
        
        OutlinedTextField(
            value = password, 
            onValueChange = { password = it }, 
            label = { Text("Mật khẩu") }, 
            visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = { Icon(if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null, Modifier.clickable { passwordVisible = !passwordVisible }) },
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )
        
        Spacer(Modifier.height(12.dp))
        
        OutlinedTextField(
            value = confirmPassword, 
            onValueChange = { confirmPassword = it }, 
            label = { Text("Nhập lại mật khẩu") },
            visualTransformation = if (confirmVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = { Icon(if (confirmVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null, Modifier.clickable { confirmVisible = !confirmVisible }) },
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )

        if (errorMsg.isNotEmpty()) Text(errorMsg, color = Color.Red, modifier = Modifier.padding(top = 8.dp), fontSize = 13.sp)

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (username.isBlank() || email.isBlank() || password != confirmPassword) {
                    errorMsg = "Vui lòng nhập đầy đủ thông tin"
                    return@Button
                }
                if (!isEmailValid) {
                    errorMsg = "Email không đúng định dạng"
                    return@Button
                }
                isLoading = true
                scope.launch {
                    val error = com.example.doan3.firebase.FirebaseManager.register(username.trim(), email.trim(), phone.trim(), password)
                    isLoading = false
                    if (error == null) {
                        showSuccess = true
                    } else {
                        errorMsg = error
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp)) 
            else Text("Đăng ký", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        Text("Đã có tài khoản? Đăng nhập", modifier = Modifier.fillMaxWidth().clickable { onGoLogin() }, textAlign = TextAlign.Center, fontSize = 14.sp)
    }
}

@Composable
fun UserProfileScreen(
    username: String,
    email: String,
    firestoreId: String = "",
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var subScreen by remember { mutableStateOf("") }

    when (subScreen) {
        "info" -> UserInfoScreen(username = username, email = email, onBack = { subScreen = "" })
        "orders" -> UserOrdersScreen(userId = firestoreId, username = username, onBack = { subScreen = "" })
        "password" -> ChangePasswordScreen(username = username, onBack = { subScreen = "" })
        else -> {
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Đăng xuất") },
                    text = { Text("Bạn có chắc muốn đăng xuất không?") },
                    confirmButton = { 
                        TextButton(onClick = { 
                            showLogoutDialog = false
                            onLogout() 
                        }) { Text("Đăng xuất", color = Color.Red) } 
                    },
                    dismissButton = { 
                        TextButton(onClick = { showLogoutDialog = false }) { Text("Hủy") } 
                    }
                )
            }

            Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F3EE))) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("ACCOUNT", fontWeight = FontWeight.Bold, fontSize = 17.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                }

                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(90.dp).clip(CircleShape).background(Color(0xFFDDDDDD)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Person, null, modifier = Modifier.size(54.dp), tint = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(username, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(email, fontSize = 13.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(28.dp))

                val menuItems = listOf(
                    Triple(Icons.Outlined.Info, "Thông tin tài khoản", "info"),
                    Triple(Icons.Outlined.Receipt, "Đơn hàng của tôi", "orders"),
                    Triple(Icons.Outlined.Lock, "Đổi mật khẩu", "password"),
                    Triple(Icons.AutoMirrored.Outlined.ExitToApp, "Đăng xuất", "logout"),
                )

                Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    menuItems.forEach { (icon, label, action) ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { 
                                if (action == "logout") showLogoutDialog = true else subScreen = action 
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(icon, null, tint = if (action == "logout") Color.Red else Color.Black)
                                Spacer(Modifier.width(14.dp))
                                Text(label, fontWeight = FontWeight.SemiBold, color = if (action == "logout") Color.Red else Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.FROYO)
@Composable
fun AccountTabScreen(
    modifier: Modifier = Modifier,
    isLoggedIn: Boolean,
    username: String,
    email: String,
    onLoginSuccess: (String, String, String, Boolean) -> Unit, 
    onLogout: () -> Unit
) {
    var subScreen by remember { mutableStateOf("main") }

    Box(modifier = modifier) {
        if (isLoggedIn) {
            val user = userAccounts.find { it.username == username }
            UserProfileScreen(
                username = username,
                email = email,
                firestoreId = user?.firestoreId ?: "",
                onLogout = onLogout
            )
        } else {
            when (subScreen) {
                "main" -> AccountMainScreen(onBack = {}, showBack = false, onLogin = { subScreen = "login" }, onRegister = { subScreen = "register" })
                "login" -> LoginScreen(
                    onBack = { subScreen = "main" },
                    onGoRegister = { subScreen = "register" },
                    onLoginSuccess = { user, em, userId, isAdmin -> 
                        onLoginSuccess(user, em, userId, isAdmin)
                    }
                )
                "register" -> RegisterScreen(onBack = { subScreen = "main" }, onGoLogin = { subScreen = "login" })
            }
        }
    }
}
