package com.icongkhanh.kmuzic.data.repositories

import com.icongkhanh.kmuzic.data.local.database.dao.kMuzicDao
import com.icongkhanh.kmuzic.data.local.database.model.PlaylistAndMusicRef
import com.icongkhanh.kmuzic.data.local.memory.MemoryMusicLoader
import com.icongkhanh.kmuzic.data.prefs.Prefs
import com.icongkhanh.kmuzic.data.utils.mapToDBModel
import com.icongkhanh.kmuzic.data.utils.mapToDomainModel
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.domain.models.Playlist
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

    override suspend fun removeFavorite(muzicId: String) {
        val music =
            withContext(Dispatchers.IO) { musicDao.getMusicById(muzicId) }.copy(isFavorite = false)
        withContext(Dispatchers.IO) { musicDao.updateMusic(music) }
    }

    override suspend fun toggleFavoriteMusic(muzicId: String) {
        val music =
            withContext(Dispatchers.IO) { musicDao.getMusicById(muzicId) }.let {
                it.copy(isFavorite = !it.isFavorite)
            }
        withContext(Dispatchers.IO) { musicDao.updateMusic(music) }
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        musicDao.insertPlaylist(playlist.mapToDBModel())
    }

    override suspend fun saveAllMusicToNowPlaylist(list: List<Music>) {
        withContext(Dispatchers.IO) {
            musicDao.deleteAllNowPlaylist()
            list.forEach {
                musicDao.insertMusicToPlaylist(
                    PlaylistAndMusicRef("8664e1aa-6fc1-4801-b9fd-c4858b92da09", it.id)
                )
            }
        }
    }

    override suspend fun saveMusicToNowPlaylist(muzicId: String) {
        withContext(Dispatchers.IO) {
            musicDao.insertMusicToPlaylist(
                PlaylistAndMusicRef("8664e1aa-6fc1-4801-b9fd-c4858b92da09", muzicId)
            )
        }
    }

    override suspend fun deleteAllNowPlaylist() {
        withContext(Dispatchers.IO) { musicDao.deleteAllNowPlaylist() }
    }

    override suspend fun deleteMusicFromNowPlaylist(muzicId: String) {
        withContext(Dispatchers.IO) { musicDao.deleteMusicFromNowPlaylist(muzicId) }
    }

    override suspend fun getNowPlaylist(): List<Music> {
        return musicDao.getNowPlaylist().listMusic.map { it.mapToDomainModel() }
    }
}
