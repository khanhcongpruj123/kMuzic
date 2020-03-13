package com.icongkhanh.kmuzic.data.repositories

import com.icongkhanh.kmuzic.data.local.memory.MemoryMusicLoader
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.domain.repositories.MuzicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class MuzicRepositoryImpl(val memoryMusicLoader: MemoryMusicLoader) : MuzicRepository {

    override suspend fun loadAllMuzic(isFirstTimeOpenApp: Boolean): Flow<List<Music>> =
        withContext(Dispatchers.IO) {
            memoryMusicLoader.getAllMusic()
        }

    override suspend fun loadMusicInPlaylist(playlistId: String): Flow<List<Music>> = flow {

    }

    override suspend fun loadFavoriteMuzic(): Flow<List<Music>> = flow {

    }

    override suspend fun addToFavorite(muzicId: String) {

    }

    override suspend fun addToPlaylist(muzicId: String, playlistId: String) {

    }

}