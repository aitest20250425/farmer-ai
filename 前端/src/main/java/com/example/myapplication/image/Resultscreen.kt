// ResultScreen.kt
package com.example.myapplication.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

@Composable
fun ResultScreen(
    navController: NavController,
    success: Boolean,
    message: String,
    imageUrl: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (success) "預測成功" else "預測失敗",
            fontSize = 20.sp,
            color = if (success) Color(0xFF4CAF50) else Color.Red
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = message, fontSize = 16.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(12.dp))

        if (success && imageUrl.isNotBlank()) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            )
        }
    }
}