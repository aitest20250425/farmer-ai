package com.example.myapplication.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.style.TextDecoration
import com.example.myapplication.apis.loginToFlask

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null)}

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF49BB4B)),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFF49BB4B))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Text("Test", fontSize = 24.sp, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("帳號") },
                maxLines = 1,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 密碼輸入框
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("密碼") },
                maxLines = 1,
                visualTransformation = PasswordVisualTransformation(), // 隱藏密碼
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    loginToFlask(username, password) { token, email, error ->
                        if (token != null && email != null) {
                            navController.navigate("main/$email")
                        } else {
                            errorMessage = error ?: "未知錯誤"
                        }
                    }
                }) {
                    Text("登入", color = Color.White)
                }


                Spacer(modifier = Modifier.padding(8.dp))
                Button(onClick = {
                    navController.navigate("register")
                }) {
                    Text("註冊")

                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = Color.Red)
            }
            TextButton(onClick = {
                navController.navigate("forgot_password")
            }) {
                Text(
                    "忘記密碼？",
                    color = Color.White,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}
