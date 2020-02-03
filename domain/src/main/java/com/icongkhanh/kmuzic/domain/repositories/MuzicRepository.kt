package com.icongkhanh.kmuzic.domain.repositories

import com.icongkhanh.kmuzic.domain.models.Muzic
import kotlinx.coroutines.flow.Flow

interface MuzicRepository {

    suspend fun loadAllMuzic(isFirstTimeOpenApp: Boolean): Flow<Muzic>
    suspend fun loadMusicInPlaylist(playlistId: String): Flow<Muzic>
    suspend fun loadFavoriteMuzic(): Flow<Muzic>
    suspend fun addToFavorite(muzicId: String)
    suspend fun addToPlaylist(muzicId: String, playlistId: String)
}