package com.example.myapplication.apis

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class ProfileViewModel : ViewModel() {
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(true)

    // 在 ViewModel 中獲取資料
    suspend fun fetchUserProfile(email: String) {
        // 這裡需要進行網絡請求，獲取資料並更新 UI
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("yourip/user/profile?email=$email")
                .build()

            val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }

            if (response.isSuccessful) {
                val responseBody = withContext(Dispatchers.IO) { response.body?.string() }
                val json = JSONObject(responseBody ?: "")
                name = json.optString("name")
                this.email = json.optString("email")

            } else {
                errorMessage = "資料獲取失敗"
            }
        } catch (e: IOException) {
            errorMessage = "網絡錯誤: ${e.message}"
        }finally {
            isLoading = false
        }
    }

    // 更新用戶資料
    fun updateUserProfile(newName: String, newEmail: String) {
        name = newName
        email = newEmail
        errorMessage = "資料已更新"
    }
}