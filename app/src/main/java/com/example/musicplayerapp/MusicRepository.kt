package com.example.musicplayerapp

import android.content.Context
import com.example.musicplayerapp.ui.theme.screens.Song
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object MusicRepository {
    suspend fun getSongs(context: Context): List<Song> {
        return try {
            // Get MP3 files from assets
            val assetFiles = context.assets.list("")?.filter { it.endsWith(".mp3") }?.map { it.removeSuffix(".mp3") } ?: emptyList()
            // Get metadata from Firestore
            val firestore = FirebaseFirestore.getInstance()
            val snapshot = firestore.collection("songs").get().await()
            val firestoreSongs = snapshot.documents.mapNotNull { it.toObject(Song::class.java) }
            // Match Firestore songs with assets
            firestoreSongs.filter { song -> assetFiles.contains(song.title) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
