package com.example.musicplayerapp.ui.theme.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.musicplayerapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

val Context.dataStore by preferencesDataStore(name = "user_prefs")
private val AVATAR_KEY = intPreferencesKey("selected_avatar")

@Composable
fun ProfileScreen(navController: NavHostController) {
    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email ?: "Unknown"
    val username = email.substringBefore("@")
    val lastLoginTime = user?.metadata?.lastSignInTimestamp?.let {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(it))
    } ?: "N/A"

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedAvatar by remember { mutableStateOf(R.drawable.avatar1) }

    // Load saved avatar
    LaunchedEffect(Unit) {
        val preferences = context.dataStore.data.first()
        selectedAvatar = preferences[AVATAR_KEY] ?: R.drawable.avatar1
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(8.dp)

            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(50.dp)
                ) {
                    Image(
                        painter = painterResource(id = selectedAvatar),
                        contentDescription = "Profile Avatar",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(username, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(email, color = Color.Gray, fontSize = 16.sp)
                    Text("Last Login: $lastLoginTime", color = Color.LightGray, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Choose your Avatar",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val avatarList = listOf(
                    R.drawable.avatar1,
                    R.drawable.avatar2,
                    R.drawable.avatar3,
                    R.drawable.avatar4,
                    R.drawable.avatar5
                )

                avatarList.forEach { avatarRes ->
                    Image(
                        painter = painterResource(id = avatarRes),
                        contentDescription = "Avatar Option",
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (avatarRes == selectedAvatar) Color(0xFFDF9FFF) else Color.DarkGray)
                            .clickable {
                                selectedAvatar = avatarRes
                                scope.launch {
                                    context.dataStore.edit { prefs ->
                                        prefs[AVATAR_KEY] = avatarRes
                                    }
                                }
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.clickable {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    scope.launch {
                        snackbarHostState.showSnackbar("Reset link sent to $email")
                    }
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LockReset,
                    contentDescription = "Reset Password",
                    tint = Color(0xFFDF9FFF)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reset Password",
                    color = Color(0xFFDF9FFF),
                    fontSize = 14.sp,
                    textDecoration = TextDecoration.Underline
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo("music") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDF9FFF)),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = "Logout")
                Spacer(Modifier.width(8.dp))
                Text("Logout", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
