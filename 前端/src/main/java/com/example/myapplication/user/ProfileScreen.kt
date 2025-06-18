package com.example.myapplication.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.apis.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, userEmail: String) {
    val profileViewModel: ProfileViewModel = viewModel()

    // 初次載入時發送請求
    LaunchedEffect(userEmail) {
        profileViewModel.fetchUserProfile(userEmail)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7FF584)),
        contentAlignment = Alignment.TopCenter
    ) {
        if (profileViewModel.isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Column(
                modifier = Modifier
                    .background(Color(0xFF7FF584))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("個人資料", fontSize = 24.sp, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))

                // 用戶頭像
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.Gray, shape = androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("頭像", color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 姓名欄位
                TextField(
                    value = profileViewModel.name,
                    onValueChange = { profileViewModel.name = it },
                    label = { Text("姓名") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 電子郵件欄位
                TextField(
                    value = profileViewModel.email,
                    onValueChange = { profileViewModel.email = it },
                    label = { Text("電子郵件") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 儲存按鈕
                Button(onClick = {
                    profileViewModel.updateUserProfile(profileViewModel.name, profileViewModel.email)
                }) {
                    Text("儲存")
                }

                profileViewModel.errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it, color = Color.Red)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 登出按鈕
                Button(onClick = {
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                }) {
                    Text("登出")
                }
            }
        }
    }
}