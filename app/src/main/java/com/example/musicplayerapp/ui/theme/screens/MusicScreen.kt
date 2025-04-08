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
import androidx.compose.material.icons.filled.*
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

@Composable
fun MusicScreen(navController: NavHostController) {
    val songs = listOf(
        Song("Shape of You", "Ed Sheeran", "Divide", "2017", "3:53", "https://upload.wikimedia.org/wikipedia/en/b/b4/Shape_Of_You_%28Official_Single_Cover%29_by_Ed_Sheeran.png"),
        Song("Blinding Lights", "The Weeknd", "After Hours", "2019", "3:20", "https://m.media-amazon.com/images/I/61C33rSMlWL.jpg"),
        Song("Someone Like You", "Adele", "21", "2011", "4:45", "https://www.fathomentertainment.com/wp-content/uploads/Mobile-App-Cinemark.jpg"),
    )

    Scaffold(bottomBar = { BottomNavBar() }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
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
                items(songs) { song ->
                    MusicTile(song = song) {
                        val encodedImageUrl = java.net.URLEncoder.encode(song.imageUrl, "UTF-8")
                        navController.navigate(
                            "playing/${song.title}/${song.artist}/${song.album}/${song.year}/${song.duration}/$encodedImageUrl"
                        )

                    }
                }
            }
        }
    }
}


@Composable
fun MusicTile(song: Song, onClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val cardSize by animateDpAsState(targetValue = if (expanded) 220.dp else 160.dp, label = "size")

    Card(
        modifier = Modifier
            .size(cardSize)
            .clickable { onClick() }, // 👈 navigate on click
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

//data class Song(
//    val title: String,
//    val artist: String,
//    val album: String,
//    val year: String,
//    val duration: String,
//    val imageUrl: String
//)
