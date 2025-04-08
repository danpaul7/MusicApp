package com.example.musicplayerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.musicplayerapp.ui.theme.screens.LoginScreen
import com.example.musicplayerapp.ui.theme.screens.MusicScreen
import com.example.musicplayerapp.ui.theme.screens.RegisterScreen
import com.example.musicplayerapp.ui.theme.screens.ForgotPasswordScreen
import com.example.musicplayerapp.ui.theme.screens.MusicPlayingScreen
import com.example.musicplayerapp.ui.theme.screens.Song

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
    composable("music") { MusicScreen(navController) }
    composable("forgot_password") { ForgotPasswordScreen(navController) }

    composable(
        route = "playing/{title}/{artist}/{album}/{year}/{duration}/{imageUrl}",
    ) { backStackEntry ->
        val args = backStackEntry.arguments!!
        val song = Song(
            title = args.getString("title")!!,
            artist = args.getString("artist")!!,
            album = args.getString("album")!!,
            year = args.getString("year")!!,
            duration = args.getString("duration")!!,
            imageUrl = java.net.URLDecoder.decode(args.getString("imageUrl")!!, "UTF-8")
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

