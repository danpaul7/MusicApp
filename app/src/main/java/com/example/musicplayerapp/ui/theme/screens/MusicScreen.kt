package com.example.musicplayerapp.ui.theme.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.musicplayerapp.ui.theme.components.BottomNavBar
import com.example.musicplayerapp.ui.theme.screens.SearchScreen
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.net.URLEncoder

@Composable
fun MusicScreen(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf(0) }
    val songsState = remember { mutableStateOf<List<Song>>(emptyList()) }
    val firestore = FirebaseFirestore.getInstance()

    // Firebase Listener
    DisposableEffect(Unit) {
        val registration: ListenerRegistration = firestore.collection("songs")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val songList = snapshot.documents.mapNotNull { it.toObject(Song::class.java) }
                songsState.value = songList
            }
        onDispose { registration.remove() }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedItem = selectedTab,
                onItemSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> MusicContent(allSongs = songsState.value, navController = navController)
                1 -> SearchScreen(allSongs = songsState.value) { song ->
                    val encodedImageUrl = URLEncoder.encode(song.imageUrl, "UTF-8")
                    val encodedSongUrl = URLEncoder.encode(song.songUrl, "UTF-8")
                    navController.navigate(
                        "playing/${song.title}/${song.artist}/${song.album}/${song.year}/${song.duration}/$encodedImageUrl/$encodedSongUrl"
                    )
                }
            }
        }
    }
}

@Composable
fun MusicContent(allSongs: List<Song>, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            "Music Picks for You",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(allSongs) { song ->
                MusicTile(song = song) {
                    val encodedImageUrl = URLEncoder.encode(song.imageUrl, "UTF-8")
                    val encodedSongUrl = URLEncoder.encode(song.songUrl, "UTF-8")
                    navController.navigate(
                        "playing/${song.title}/${song.artist}/${song.album}/${song.year}/${song.duration}/$encodedImageUrl/$encodedSongUrl"
                    )
                }
            }
        }
    }
}

@Composable
fun MusicTile(song: Song, onClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val cardSize by animateDpAsState(targetValue = if (expanded) 220.dp else 160.dp, label = "cardSize")

    Card(
        modifier = Modifier
            .size(cardSize)
            .clickable {
                expanded = !expanded
                onClick()
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF252525)),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                Box {
                    AsyncImage(
                        model = song.imageUrl,
                        contentDescription = "Album Art",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(Color.DarkGray)
                    )
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.Center)
                            .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(50))
                            .padding(4.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .defaultMinSize(minHeight = if (expanded) 80.dp else 50.dp)
                ) {
                    Text(song.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(song.artist, color = Color.Gray, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    if (expanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Album: ${song.album}", color = Color.White, fontSize = 12.sp)
                        Text("Year: ${song.year}", color = Color.White, fontSize = 12.sp)
                        Text("Duration: ${song.duration}", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

data class Song(
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val year: String = "",
    val duration: String = "",
    val imageUrl: String = "",
    val songUrl: String = ""
)
