package com.example.musicplayerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicplayerapp.ui.theme.screens.*
import com.google.firebase.auth.FirebaseAuth

object Routes {
    const val SESSION = "session"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val MUSIC = "music"
    const val PLAYING = "playing/{index}" // Changed to use index
    const val PROFILE = "profile"
}

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
    NavHost(navController = navController, startDestination = "session") {
        composable("session") { SessionHandler(navController) }
        mainNavGraph(navController)
    }
}

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    composable("login") { LoginScreen(navController) }
    composable("register") { RegisterScreen(navController) }
    composable("forgot_password") { ForgotPasswordScreen(navController) }
    composable("music") {
        MusicScreen(navController = navController)
    }
    composable("playing/{index}") { backStackEntry ->
        val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
        MusicPlayingScreen(index = index, navController = navController)
    }
}

@Composable
fun SessionHandler(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.navigate(Routes.MUSIC) {
                popUpTo(Routes.SESSION) { inclusive = true }
            }
        } else {
            navController.navigate(Routes.LOGIN) {
                popUpTo(Routes.SESSION) { inclusive = true }
            }
        }
    }
}