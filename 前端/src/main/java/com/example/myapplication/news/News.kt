// NewsScreen.kt
package com.example.myapplication.news

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NewsScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            return false // 所有網址都在 WebView 中開啟
                        }
                    }
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true // 若網站有使用 localStorage / sessionStorage
                    loadUrl("https://www.moa.gov.tw/theme_list.php?theme=news&sub_theme=agri")
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
