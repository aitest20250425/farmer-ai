package com.example.myapplication.apis

import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

// 註冊功能：將 Email、密碼、驗證碼送給 Flask 後端
fun registerToFlask(name: String, email: String, password: String, verificationCode: String, onResult: (Boolean, String) -> Unit) {
    val client = OkHttpClient()
    val mediaType = "application/json".toMediaType()
    val requestBody = """
        {
            "name": "$name",
            "email": "$email",
            "password": "$password",
            "verification_code": "$verificationCode"
        }
    """.trimIndent().toRequestBody(mediaType)

    val request = Request.Builder()
        .url("yourip/register")
        .post(requestBody)
        .build()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && body != null) {
                        val msg = JSONObject(body).optString("msg", "註冊成功")
                        onResult(true, msg)
                    } else {
                        val msg = body?.let {
                            try {
                                JSONObject(it).optString("msg", "註冊失敗")
                            } catch (e: Exception) {
                                "註冊失敗"
                            }
                        } ?: "註冊失敗"
                        onResult(false, msg)
                    }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onResult(false, "無法連接伺服器：${e.localizedMessage}")
            }
        }
    }
}

// 發送驗證碼功能：只送 Email 到 Flask，後端會處理發送
fun sendVerificationCodeToFlask(email: String, callback: (Boolean, String?) -> Unit) {
    val client = OkHttpClient()
    val mediaType = "application/json".toMediaType()
    val requestBody = JSONObject().apply {
        put("email", email)
    }.toString().toRequestBody(mediaType)

    val request = Request.Builder()
        .url("yourip/send_verification_code")
        .post(requestBody)
        .build()

    // 不需要使用 Dispatchers.IO，因為 enqueue() 已經是異步的
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback(false, "發送驗證碼失敗: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string()
            if (response.isSuccessful && body != null) {
                val msg = JSONObject(body).optString("msg", "驗證碼已發送")
                callback(true, msg)
            } else {
                callback(false, "發送驗證碼失敗: ${response.message}")
            }
        }
    })
}

