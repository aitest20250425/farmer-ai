package com.example.myapplication.ui

import androidx.compose.material3.Button
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.login.LoginScreen
import com.example.myapplication.login.RegisterScreen
import com.example.myapplication.user.ProfileScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.navigation.NavType
import android.net.Uri
import androidx.navigation.navArgument
import com.example.myapplication.image.LoadingScreen
import com.example.myapplication.image.ResultScreen
import com.example.myapplication.image.UploadImageScreen
import com.example.myapplication.login.ForgotPasswordScreen
import com.example.myapplication.news.NewsScreen
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("main/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            MyScreen(navController, userEmail = email)
        }
        composable("profile/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ProfileScreen(navController = navController, userEmail = email)
        }
        composable("news") { NewsScreen(navController) }
        composable("forgot_password") { ForgotPasswordScreen(navController) }
        composable("upload_image") { UploadImageScreen(navController) }

        // 加入以下兩個
        composable("loading_screen/{imagePath}") { backStackEntry ->
            val encodedPath = backStackEntry.arguments?.getString("imagePath") ?: ""
            val imagePath = Uri.decode(encodedPath)
            LoadingScreen(navController, imagePath)
        }


        composable(
            "result_screen/{success}/{message}/{imageUrl}",
            arguments = listOf(
                navArgument("success") { type = NavType.StringType },
                navArgument("message") { type = NavType.StringType },
                navArgument("imageUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val success = backStackEntry.arguments?.getString("success")?.toBoolean() ?: false
            val message = Uri.decode(backStackEntry.arguments?.getString("message") ?: "")
            val imageUrl = Uri.decode(backStackEntry.arguments?.getString("imageUrl") ?: "")

            ResultScreen(
                navController = navController,
                success = success,
                message = message,
                imageUrl = imageUrl
            )
        }
    }

}

@Composable
fun MyScreen(navController: NavController, userEmail: String) {

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenHeight = maxHeight

        Column(modifier = Modifier.fillMaxSize(),verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.12f)
                    .background(Color.Green),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .height(80.dp)
                            .aspectRatio(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "PestiWise農安智選",
                        color = Color.Black,
                        fontSize = 28.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .background(Color.White),
                verticalArrangement = Arrangement.spacedBy(10.dp) // 每列之間留空
            ) {



                Row(
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    ExampleButton(
                        text = "個人資訊",
                        color = Color(0xFF7FF584),
                        onClick = { navController.navigate("profile/${userEmail}") },
                        modifier = Modifier.weight(1f)
                    )
                    ExampleButton(
                        text = "登出",
                        color = Color(0xFF7FF584),
                        onClick = {navController.navigate("login")},
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    ExampleButton(
                        text = "農業新聞",
                        color = Color(0xFF7FF584),
                        onClick = { navController.navigate("news") },
                        modifier = Modifier.weight(1f)
                    )
                    ExampleButton(
                        text = "圖像查詢",
                        color = Color(0xFF7FF584),
                        onClick = { navController.navigate("upload_image") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    ExampleButton(
                        text = "功能5",
                        color = Color(0xFF7FF584),
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    )
                    ExampleButton(
                        text = "功能6",
                        color = Color(0xFF7FF584),
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ExampleButton(
    text: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f)
            .padding(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(12.dp), // 可選：圓角
        contentPadding = PaddingValues(0.dp) // 讓內容撐滿
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}
