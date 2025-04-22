package com.example.musicplayerapp.ui.theme.screens

import android.media.AudioManager
import android.media.MediaPlayer
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayingScreen(song: Songs) { // Changed from Songs to Song
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var currentTime by remember { mutableStateOf("0:00") }
    val mediaPlayer = remember { MediaPlayer() }
    val isPrepared = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Construct local asset path based on song title
    val localSongPath = "${song.title}.mp3" // e.g., "Shape of You.mp3"

    DisposableEffect(localSongPath) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            // Load from assets instead of songUrl
            val assetFileDescriptor = context.assets.openFd(localSongPath)
            mediaPlayer.setDataSource(
                assetFileDescriptor.fileDescriptor,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.length
            )
            mediaPlayer.setOnPreparedListener {
                isPrepared.value = true
                mediaPlayer.start()
                isPlaying = true
            }
            mediaPlayer.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle case where file is not found; for now, print error
        }

        onDispose {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    // Auto update progress
    LaunchedEffect(isPlaying) {
        while (isPlaying && mediaPlayer.isPlaying) {
            progress = mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration
            currentTime = formatMillis(mediaPlayer.currentPosition)
            delay(1000L)
        }
    }

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
                var userSeeking by remember { mutableStateOf(false) }
                var seekPosition by remember { mutableStateOf(0f) }
                var wasPlayingBeforeSeek by remember { mutableStateOf(false) }

                Slider(
                    value = if (userSeeking) seekPosition else progress,
                    onValueChange = {
                        if (isPrepared.value) {
                            userSeeking = true
                            seekPosition = it
                        }
                    },
                    onValueChangeFinished = {
                        if (isPrepared.value) {
                            val newPosition = (seekPosition * mediaPlayer.duration).toInt()
                            wasPlayingBeforeSeek = mediaPlayer.isPlaying

                            mediaPlayer.pause()
                            mediaPlayer.seekTo(newPosition)

                            coroutineScope.launch {
                                delay(300L)
                                if (wasPlayingBeforeSeek) {
                                    mediaPlayer.start()
                                }
                                isPlaying = mediaPlayer.isPlaying
                            }

                            progress = seekPosition
                        }
                        userSeeking = false
                    },
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
                    Text(currentTime, color = Color.Gray, fontSize = 12.sp)
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
                IconButton(onClick = {
                    mediaPlayer.seekTo(0)
                }) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = Color.White, modifier = Modifier.size(40.dp))
                }

                IconButton(onClick = {
                    if (isPrepared.value) {
                        if (mediaPlayer.isPlaying) {
                            mediaPlayer.pause()
                            isPlaying = false
                        } else {
                            mediaPlayer.start()
                            isPlaying = true
                        }
                    }
                }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                }

                IconButton(onClick = {
                    mediaPlayer.seekTo(mediaPlayer.duration)
                }) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(40.dp))
                }
            }
        }
    }
}

fun formatMillis(millis: Int): String {
    val minutes = millis / 1000 / 60
    val seconds = (millis / 1000) % 60
    return String.format("%d:%02d", minutes, seconds)
}