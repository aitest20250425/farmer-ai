package com.example.myapplication.apis

import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

// 寄送重設密碼驗證碼
fun sendResetPasswordCodeToFlask(email: String, callback: (Boolean, String?) -> Unit) {
    val client = OkHttpClient()
    val mediaType = "application/json".toMediaType()
    val requestBody = JSONObject().apply {
        put("email", email)
    }.toString().toRequestBody(mediaType)

    val request = Request.Builder()
        .url("http://163.13.143.191:7777/send_reset_password_code")
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback(false, "驗證碼發送失敗: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string()
            if (response.isSuccessful && body != null) {
                val msg = JSONObject(body).optString("msg", "驗證碼已發送")
                callback(true, msg)
            } else {
                callback(false, "驗證碼發送失敗: ${response.message}")
            }
        }
    })
}

// 重設密碼
fun resetPasswordToFlask(
    email: String,
    newPassword: String,
    verificationCode: String,
    onResult: (Boolean, String) -> Unit
) {
    val client = OkHttpClient()
    val mediaType = "application/json".toMediaType()
    val requestBody = JSONObject().apply {
        put("email", email)
        put("new_password", newPassword)
        put("verification_code", verificationCode)
    }.toString().toRequestBody(mediaType)

    val request = Request.Builder()
        .url("yourip/reset_password")
        .post(requestBody)
        .build()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && body != null) {
                        val msg = JSONObject(body).optString("msg", "密碼重設成功")
                        onResult(true, msg)
                    } else {
                        val msg = body?.let {
                            try {
                                JSONObject(it).optString("msg", "重設失敗")
                            } catch (e: Exception) {
                                "重設失敗"
                            }
                        } ?: "重設失敗"
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
