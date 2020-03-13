package com.icongkhanh.kmuzic.domain.repositories

import com.icongkhanh.kmuzic.domain.models.Music
import kotlinx.coroutines.flow.Flow

interface MuzicRepository {

    suspend fun loadAllMuzic(isFirstTimeOpenApp: Boolean): Flow<List<Music>>
    suspend fun loadMusicInPlaylist(playlistId: String): Flow<List<Music>>
    suspend fun loadFavoriteMuzic(): Flow<List<Music>>
    suspend fun addToFavorite(muzicId: String)
    suspend fun addToPlaylist(muzicId: String, playlistId: String)
}