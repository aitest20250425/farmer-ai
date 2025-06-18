package com.example.myapplication.image

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import java.io.File

@Composable
fun UploadImageScreen(navController: NavController) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var resultMessage by remember { mutableStateOf("") }
    var resultImageUrl by remember { mutableStateOf("") }

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("選擇一張圖片進行上傳", fontSize = 20.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("挑選圖片", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedImageUri?.let { uri ->
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "預覽圖片",
                modifier = Modifier.height(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            selectedImageUri?.let { uri ->
                getFileFromUri(uri, context)?.let { file ->
                    val encodedPath = Uri.encode(file.absolutePath)
                    navController.navigate("loading_screen/$encodedPath")
                }
            }
        }) {
            Text("上傳圖片", fontSize = 16.sp)
        }


        Spacer(modifier = Modifier.height(16.dp))
        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color(0xFF4CAF50))
                Spacer(modifier = Modifier.height(8.dp))
                Text("模型預測中，請稍候...", color = Color.Gray)
            }
        }
        Text(resultMessage, fontSize = 16.sp, color = Color.Black)

        if (resultImageUrl.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = rememberAsyncImagePainter(resultImageUrl),
                contentDescription = "病害圖片",
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            )
        }
    }
}

fun getFileFromUri(uri: Uri, context: android.content.Context): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        tempFile.outputStream().use { inputStream.copyTo(it) }
        tempFile
    } catch (e: Exception) {
        null
    }
}
