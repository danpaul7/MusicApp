package com.example.musicplayerapp.ui.theme.screens

import android.media.AudioAttributes
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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.musicplayerapp.MusicRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayingScreen(index: Int, navController: NavHostController) {
    val context = LocalContext.current
    var songs by remember { mutableStateOf<List<Song>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()
    val snackbarHostState = remember { SnackbarHostState() }

    // Load songs from MusicRepository
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            songs = MusicRepository.getSongs(context)
        }
    }

    val song = songs.getOrNull(index)

    // If no song at index, go back to music screen
    if (song == null && songs.isNotEmpty()) {
        LaunchedEffect(Unit) {
            navController.navigate("music") {
                popUpTo("playing/$index") { inclusive = true }
            }
        }
        return
    }

    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var currentTime by remember { mutableStateOf("0:00") }
    val mediaPlayer = remember { MediaPlayer() }
    val isPrepared = remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }

    // Derive a valid Firestore document ID
    val songId = song?.id?.takeIf { it.isNotBlank() } ?: song?.title?.toLowerCase(Locale.US)?.replace(" ", "_") ?: ""

    // Fetch like status for the current song from Firestore
    LaunchedEffect(song, songId) {
        if (song != null && songId.isNotEmpty()) {
            try {
                println("Fetching like status for song ID: $songId")
                val songDoc = firestore.collection("songs").document(songId)
                val snapshot = songDoc.get().await()
                isLiked = snapshot.getBoolean("liked") ?: false
                println("Fetched like status for song $songId: isLiked=$isLiked")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error fetching like status for song $songId: ${e.message}")
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Failed to fetch like status: ${e.message}")
                }
            }
        } else if (song != null) {
            println("Invalid song ID for song: ${song.title}")
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Invalid song ID for ${song.title}")
            }
        }
    }

    if (song != null && songId.isNotEmpty()) {
        val localSongPath = "${song.title}.mp3"

        DisposableEffect(localSongPath) {
            try {
                mediaPlayer.reset()
                mediaPlayer.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
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
            }

            onDispose {
                mediaPlayer.stop()
                mediaPlayer.release()
            }
        }

        LaunchedEffect(isPlaying) {
            while (isPlaying && mediaPlayer.isPlaying) {
                progress = mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration
                currentTime = formatMillis(mediaPlayer.currentPosition)
                delay(1000L)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                model = song?.imageUrl ?: "",
                contentDescription = "Album Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(300.dp)
                    .background(Color.Gray, shape = RoundedCornerShape(16.dp))
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    song?.title ?: "Loading...",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    song?.artist ?: "",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    if (song != null) "Album: ${song.album} (${song.year})" else "",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }

            Column {
                var userSeeking by remember { mutableStateOf(false) }
                var seekPosition by remember { mutableFloatStateOf(0f) }
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
                                if (wasPlayingBeforeSeek) mediaPlayer.start()
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
                    Text(song?.duration ?: "0:00", color = Color.Gray, fontSize = 12.sp)
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
                    if (index > 0) {
                        navController.navigate("playing/${index - 1}") {
                            popUpTo("playing/$index") { inclusive = true }
                        }
                    }
                }) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
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
                    if (index < songs.size - 1) {
                        navController.navigate("playing/${index + 1}") {
                            popUpTo("playing/$index") { inclusive = true }
                        }
                    }
                }) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                if (song != null && songId.isNotEmpty()) {
                    var isUpdating by remember { mutableStateOf(false) }
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                isUpdating = true
                                try {
                                    println("Like button clicked for song ID: $songId")
                                    val songDocRef = firestore.collection("songs").document(songId)
                                    val newLikeStatus = !isLiked
                                    // Optimistically update UI
                                    isLiked = newLikeStatus
                                    println("Attempting to set liked=$newLikeStatus for song $songId")
                                    // Update Firestore, creating document if it doesn't exist
                                    songDocRef.set(
                                        mapOf(
                                            "liked" to newLikeStatus,
                                            "album" to song.album,
                                            "artist" to song.artist,
                                            "duration" to song.duration,
                                            "imageUrl" to song.imageUrl,
                                            "title" to song.title,
                                            "year" to song.year
                                        ),
                                        SetOptions.merge()
                                    ).await()
                                    println("Successfully updated liked=$newLikeStatus for song $songId")
                                } catch (e: Exception) {
                                    // Revert UI on error
                                    isLiked = !isLiked
                                    e.printStackTrace()
                                    println("Error updating like status for song $songId: ${e.message}")
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Failed to update like status: ${e.message}")
                                    }
                                } finally {
                                    isUpdating = false
                                }
                            }
                        },
                        enabled = !isUpdating
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isLiked) "Unlike" else "Like",
                            tint = if (isLiked) Color.Red else Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }
    }
}

fun formatMillis(millis: Int): String {
    val minutes = millis / 1000 / 60
    val seconds = (millis / 1000) % 60
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}