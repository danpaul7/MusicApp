package com.example.musicplayerapp.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayingScreen(song: Song) {
    var isPlaying by remember { mutableStateOf(true) }
    var progress by remember { mutableStateOf(0.3f) } // Just for UI

    Scaffold(
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AsyncImage(
                model = song.imageUrl,
                contentDescription = "Album Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(300.dp)
                    .background(Color.Gray, shape = RoundedCornerShape(16.dp))
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(song.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(song.artist, fontSize = 16.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Album: ${song.album} (${song.year})", color = Color.LightGray, fontSize = 14.sp)
            }

            Column {
                Slider(
                    value = progress,
                    onValueChange = { progress = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.DarkGray
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("0:00", color = Color.Gray, fontSize = 12.sp)
                    Text(song.duration, color = Color.Gray, fontSize = 12.sp)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Previous */ }) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = Color.White, modifier = Modifier.size(40.dp))
                }
                IconButton(onClick = { isPlaying = !isPlaying }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                }
                IconButton(onClick = { /* Next */ }) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(40.dp))
                }
            }
        }
    }
}
