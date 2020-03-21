package com.icongkhanh.kmuzic.domain.repositories

import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface MuzicRepository {

    suspend fun loadAllMuzic(foreUpdate: Boolean): Flow<List<Music>>
    suspend fun loadMusicInPlaylist(playlistId: String): Flow<List<Music>>
    suspend fun loadFavoriteMuzic(): Flow<List<Music>>
    suspend fun addToFavorite(muzicId: String)
    suspend fun addToPlaylist(muzicId: String, playlistId: String)
    suspend fun getMusicById(id: String): Flow<Music>
    suspend fun removeFavorite(muzicId: String)
    suspend fun toggleFavoriteMusic(muzicId: String)
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun saveAllMusicToNowPlaylist(list: List<Music>)
    suspend fun saveMusicToNowPlaylist(muzicId: String)
    suspend fun deleteAllNowPlaylist()
    suspend fun deleteMusicFromNowPlaylist(muzicId: String)
    suspend fun getNowPlaylist(): List<Music>
}
