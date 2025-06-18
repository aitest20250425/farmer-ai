package com.example.myapplication.login
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.apis.resetPasswordToFlask
import com.example.myapplication.apis.sendResetPasswordCodeToFlask

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    fun sendVerificationCode() {
        if (email.isNotEmpty()) {
            sendResetPasswordCodeToFlask(email) { success, msg ->
                message = msg
            }
        } else {
            message = "請輸入有效的 Email"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF49BB4B))
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextField(
                value = verificationCode,
                onValueChange = { verificationCode = it },
                label = { Text("驗證碼") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                sendVerificationCode()
            }) {
                Text("取得驗證碼")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("新密碼") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("確認新密碼") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (newPassword != confirmPassword) {
                message = "密碼與確認密碼不一致"
            } else if (email.isEmpty() || verificationCode.isEmpty() || newPassword.isEmpty()) {
                message = "請填寫所有欄位"
            } else {
                resetPasswordToFlask(email, newPassword, verificationCode) { success, msg ->
                    message = msg
                    if (success) {
                        navController.navigate("login")
                    }
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("重設密碼")
        }

        message?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { navController.navigate("login") }) {
            Text("返回登入", color = Color.White)
        }
    }
}
