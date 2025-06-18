package com.example.myapplication.login
import kotlinx.coroutines.delay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.text.style.TextDecoration
import com.example.myapplication.apis.registerToFlask
import com.example.myapplication.apis.sendVerificationCodeToFlask

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var usermail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isButtonEnabled by remember { mutableStateOf(true) }
    var isCountingDown by remember { mutableStateOf(false) }
    var countdownSeconds by remember { mutableStateOf(60) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7FF584)),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFF49BB4B))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text("註冊", fontSize = 24.sp, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("名稱") },
                maxLines = 1,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = usermail,
                onValueChange = { usermail = it },
                label = { Text("Email") },
                maxLines = 1,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("密碼") },
                maxLines = 1,
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("確認密碼") },
                maxLines = 1,
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            LaunchedEffect(isCountingDown) {
                if (isCountingDown) {
                    while (countdownSeconds > 0) {
                        delay(1000)
                        countdownSeconds--
                    }
                    isButtonEnabled = true
                    isCountingDown = false
                    countdownSeconds = 60
                }
            }
            // 驗證碼欄位與按鈕 (固定顯示)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = verificationCode,
                    onValueChange = { verificationCode = it },
                    label = { Text("驗證碼") },
                    maxLines = 1,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (isButtonEnabled) {
                            sendVerificationCodeToFlask(usermail) { success, message ->
                                errorMessage = message
                                if (success) {
                                    countdownSeconds = 60              // 👈 成功後才開始倒數
                                    isButtonEnabled = false
                                    isCountingDown = true
                                }
                            }
                        }
                    },
                    modifier = Modifier.height(56.dp),
                    enabled = isButtonEnabled
                ) {
                    Text(
                        if (isButtonEnabled) "傳送驗證碼"
                        else "已發送 (${countdownSeconds}s)"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (username.isEmpty() || usermail.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || verificationCode.isEmpty()) {
                    errorMessage = "請填寫所有欄位"
                } else if (password != confirmPassword) {
                    errorMessage = "密碼不一致"
                } else {
                    registerToFlask(
                        username,
                        usermail,
                        password,
                        verificationCode
                    ) { success, message ->
                        if (success) {
                            navController.navigate("login")
                        } else {
                            errorMessage = message
                        }
                    }
                }
            }) {
                Text("註冊")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { navController.navigate("login") }) {
                Text("返回登入", color = Color.White, textDecoration = TextDecoration.Underline)
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = Color.Red)
            }
        }
    }
}
