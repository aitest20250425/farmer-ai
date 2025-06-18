package com.example.myapplication.apis

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File

suspend fun uploadImage(imagePath: String): Pair<Boolean, Pair<String, String>> {
    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val file = File(imagePath)
        if (!file.exists()) {
            return@withContext Pair(false, "檔案不存在" to "")
        }

        val mediaType = "image/*".toMediaTypeOrNull()
        val fileBody = file.asRequestBody(mediaType)

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", file.name, fileBody)
            .build()

        val request = Request.Builder()
            .url("yourip/predict")
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val result = response.body?.string() ?: ""
                val json = JSONObject(result)
                val name = json.optString("name", "未知病害")
                val confidence = json.optDouble("confidence", 0.0)
                val imageUrl = json.optString("image_url", "")
                val message = """
                    病害名稱：$name
                    信賴度：${String.format("%.2f", confidence * 100)}
                """.trimIndent()
                Pair(true, message to imageUrl)
            } else {
                Pair(false, "伺服器錯誤: ${response.code}" to "")
            }
        } catch (e: Exception) {
            Pair(false, "網路錯誤: ${e.message}" to "")
        }
    }
}
