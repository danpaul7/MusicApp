package com.example.musicplayerapp.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.musicplayerapp.MusicRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Songs(
    val id: String,
    val album: String,
    val artist: String,
    val duration: String,
    val imageUrl: String,
    val liked: Boolean,
    val title: String,
    val year: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicLibraryScreen(navController: NavHostController) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val coroutineScope = rememberCoroutineScope()
    var likedSongs by remember { mutableStateOf<List<Song>>(emptyList()) }
    var allSongs by remember { mutableStateOf<List<Song>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    var fetchTrigger by remember { mutableStateOf(0) } // For refresh button

    // Function to fetch liked songs
    fun fetchLikedSongs() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                println("Querying Firestore for liked songs (trigger: $fetchTrigger)")
                val snapshot = firestore.collection("songs")
                    .whereEqualTo("liked", true)
                    .get()
                    .await()
                likedSongs = snapshot.documents.mapNotNull { doc ->
                    try {
                        val song = Song(
                            id = doc.id,
                            album = doc.getString("album") ?: "",
                            artist = doc.getString("artist") ?: "",
                            duration = doc.getString("duration") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: "",
                            liked = doc.getBoolean("liked") ?: false,
                            title = doc.getString("title") ?: "",
                            year = doc.getString("year") ?: ""
                        )
                        println("Parsed song: ${song.title} (ID: ${song.id}, liked: ${song.liked})")
                        song
                    } catch (e: Exception) {
                        println("Error parsing song ${doc.id}: ${e.message}")
                        null
                    }
                }
                println("Fetched ${likedSongs.size} liked songs from Firestore")
                // Fallback: Check MusicRepository if Firestore is empty
                if (likedSongs.isEmpty()) {
                    println("No liked songs in Firestore, checking MusicRepository")
                    likedSongs = allSongs.filter { it.liked }
                    println("Found ${likedSongs.size} liked songs in MusicRepository")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error fetching liked songs from Firestore: ${e.message}")
                errorMessage = "Failed to load liked songs: ${e.message}"
                snackbarHostState.showSnackbar(errorMessage ?: "Unknown error")
            } finally {
                isLoading = false
            }
        }
    }

    // Fetch all songs from MusicRepository for navigation
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                allSongs = MusicRepository.getSongs(context)
                println("Fetched ${allSongs.size} songs from MusicRepository")
                allSongs.forEach { println("All song: ${it.title} (ID: ${it.id}, liked: ${it.liked})") }
            } catch (e: Exception) {
                println("Error fetching all songs from MusicRepository: ${e.message}")
                errorMessage = "Failed to load songs: ${e.message}"
                snackbarHostState.showSnackbar(errorMessage ?: "Unknown error")
            }
        }
    }

    // Fetch liked songs initially and on refresh
    LaunchedEffect(fetchTrigger) {
        fetchLikedSongs()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Liked Podcasts") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { fetchTrigger++ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: $errorMessage", fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { fetchTrigger++ }) {
                            Text("Retry")
                        }
                    }
                }
            }
            likedSongs.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No liked podcast yet!", fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Like some songs in the player to see them here.", fontSize = 14.sp)
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items(likedSongs) { song ->
                        val index = allSongs.indexOfFirst { it.id == song.id }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable {
                                    if (index != -1) {
                                        navController.navigate("playing/$index") // or use Routes.PLAYING.replace("{index}", "$index")
                                    }
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = song.title,
                                    fontSize = 18.sp,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${song.artist} • ${song.album}",
                                    fontSize = 14.sp,
                                    color = Color.LightGray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}