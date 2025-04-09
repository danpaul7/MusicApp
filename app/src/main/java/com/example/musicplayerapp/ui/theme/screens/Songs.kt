package com.example.musicplayerapp.ui.theme.screens

data class Songs(
    val title: String,
    val artist: String,
    val album: String,
    val year: String,
    val duration: String,
    val imageUrl: String,
    val songUrl: String // 🔥 URL to the mp3 file
)
