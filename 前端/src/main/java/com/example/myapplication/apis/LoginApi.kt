package com.example.myapplication.apis

import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

fun loginToFlask(
    username: String,
    password: String,
    onResult: (token: String?, email: String?, error: String?) -> Unit
){
    val client = OkHttpClient()
    val mediaType = "application/json".toMediaType()
    val requestBody = """
        {
            "email": "$username",
            "password": "$password"
        }
    """.trimIndent().toRequestBody(mediaType)

    val request = Request.Builder()
        .url("yourip/login")
        .post(requestBody)
        .build()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    val token = JSONObject(body).getString("access_token")
                    withContext(Dispatchers.Main) {
                        onResult(token, username, null)  //
                    }
                } else {
                    val errorMessage = JSONObject(body).optString("msg", "登入失敗")
                    withContext(Dispatchers.Main) {
                        onResult(null, null, errorMessage)
                    }
                }
            }
        } catch (e: Exception) {
            // 如果發生錯誤，傳回 null 的 token 和錯誤訊息
            withContext(Dispatchers.Main) {
                onResult(null, null, "無法連接伺服器")
            }
        }
    }
}
