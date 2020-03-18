package com.icongkhanh.kmuzic.data.repositories

import com.icongkhanh.kmuzic.data.local.database.dao.kMuzicDao
import com.icongkhanh.kmuzic.data.local.memory.MemoryMusicLoader
import com.icongkhanh.kmuzic.data.prefs.Prefs
import com.icongkhanh.kmuzic.data.utils.mapToDBModel
import com.icongkhanh.kmuzic.data.utils.mapToDomainModel
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.domain.repositories.MuzicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MuzicRepositoryImpl(
    private val memoryMusicLoader: MemoryMusicLoader,
    private val musicDao: kMuzicDao,
    private val prefs: Prefs
) :
    MuzicRepository {

    override suspend fun loadAllMuzic(foreUpdate: Boolean): Flow<List<Music>> =
        flow {
            val isFistTimeOpenApp = prefs.isFirstTimeOpenApp()
            if (foreUpdate || isFistTimeOpenApp) {
                val listMusic = withContext(Dispatchers.IO) { memoryMusicLoader.getAllMusic() }
                listMusic.forEach {
                    withContext(Dispatchers.IO) { musicDao.insertMusic(it.mapToDBModel()) }
                }
                prefs.setIsFirstTimeOpenApp(false)
            }
            emitAll(
                withContext(Dispatchers.IO) { musicDao.getAllMusic() }
                    .map { list -> list.map { it.mapToDomainModel() } }
            )
        }

    override suspend fun loadMusicInPlaylist(playlistId: String): Flow<List<Music>> = flow {

    }

    override suspend fun loadFavoriteMuzic(): Flow<List<Music>> = flow {
        val res = withContext(Dispatchers.IO) { musicDao.getFavoriteMusic() }
        emitAll(res.map { list -> list.map { it.mapToDomainModel() } })
    }

    override suspend fun addToFavorite(muzicId: String) {
        val music = withContext(Dispatchers.IO) { musicDao.getMusicById(muzicId) }
        val updateMusic = music.copy(isFavorite = true)
        withContext(Dispatchers.IO) { musicDao.updateMusic(updateMusic) }
    }

    override suspend fun addToPlaylist(muzicId: String, playlistId: String) {

    }

    override suspend fun getMusicById(id: String): Flow<Music> =
        flow {
            emit(withContext(Dispatchers.IO) { musicDao.getMusicById(id) }.mapToDomainModel())
        }
}
