// LoadingScreen.kt
package com.example.myapplication.image

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.net.Uri
import com.example.myapplication.apis.uploadImage

@Composable
fun LoadingScreen(
    navController: NavController,
    imagePath: String
) {
    LaunchedEffect(true) {
        val (success, result) = uploadImage(imagePath)
        val (message, imageUrl) = result

        val encodedMessage = Uri.encode(message)
        val encodedImageUrl = Uri.encode(imageUrl)

        navController.navigate("result_screen/$success/$encodedMessage/$encodedImageUrl")
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color(0xFF4CAF50))
            Spacer(modifier = Modifier.height(12.dp))
            Text("模型預測中...", fontSize = 18.sp, color = Color.DarkGray)
        }
    }
}
