package com.icongkhanh.kmuzic.data.repositories

import com.icongkhanh.kmuzic.data.local.memory.MemoryMusicLoader
import com.icongkhanh.kmuzic.domain.models.Muzic
import com.icongkhanh.kmuzic.domain.repositories.MuzicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class MuzicRepositoryImpl(val memoryMusicLoader: MemoryMusicLoader) : MuzicRepository {
    override suspend fun loadAllMuzic(isFirstTimeOpenApp: Boolean): Flow<Muzic> = memoryMusicLoader.execute()

    override suspend fun loadMusicInPlaylist(playlistId: String): Flow<Muzic> = flow {
        memoryMusicLoader.execute().collect {

        }
    }

    override suspend fun loadFavoriteMuzic(): Flow<Muzic> = flow {
        memoryMusicLoader.execute().collect {
            if (it.isFavorite) emit(it)
        }
    }

    override suspend fun addToFavorite(muzicId: String) {

    }

    override suspend fun addToPlaylist(muzicId: String, playlistId: String) {

    }

}