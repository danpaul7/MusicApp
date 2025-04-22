import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("liked_songs")

object LikedSongsDataStore {
    private val LIKED_SONGS_KEY = stringSetPreferencesKey("liked_songs")

    suspend fun addLikedSong(context: Context, songTitle: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[LIKED_SONGS_KEY] ?: emptySet()
            preferences[LIKED_SONGS_KEY] = current + songTitle
        }
    }

    suspend fun removeLikedSong(context: Context, songTitle: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[LIKED_SONGS_KEY] ?: emptySet()
            preferences[LIKED_SONGS_KEY] = current - songTitle
        }
    }

    fun getLikedSongs(context: Context): Flow<Set<String>> {
        return context.dataStore.data.map { preferences ->
            preferences[LIKED_SONGS_KEY] ?: emptySet()
        }
    }
}
