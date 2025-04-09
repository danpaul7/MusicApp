package com.example.musicplayerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicplayerapp.ui.theme.screens.*
import java.net.URLDecoder
import java.net.URLEncoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {

    composable("login") { LoginScreen(navController) }

    composable("register") { RegisterScreen(navController) }

    composable("forgot_password") { ForgotPasswordScreen(navController) }

    composable("music") {
        MusicScreen(navController = navController)
    }


    composable(
        route = "playing/{title}/{artist}/{album}/{year}/{duration}/{imageUrl}/{songUrl}"
    ) { backStackEntry ->
        val args = backStackEntry.arguments!!
        val song = Songs(
            title = args.getString("title") ?: "",
            artist = args.getString("artist") ?: "",
            album = args.getString("album") ?: "",
            year = args.getString("year") ?: "",
            duration = args.getString("duration") ?: "",
            imageUrl = URLDecoder.decode(args.getString("imageUrl") ?: "", "UTF-8"),
            //songUrl = URLDecoder.decode(args.getString("songUrl") ?: "", "UTF-8")
                    songUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",

            )
        MusicPlayingScreen(song)
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "login") {
        mainNavGraph(navController)
    }
}
