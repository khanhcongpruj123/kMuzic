package com.icongkhanh.kmuzic.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.icongkhanh.kmuzic.data.local.database.model.MusicDB
import com.icongkhanh.kmuzic.data.local.database.model.PlaylistAndMusicRef
import com.icongkhanh.kmuzic.data.local.database.model.PlaylistDB
import com.icongkhanh.kmuzic.data.local.database.model.PlaylistWithMusic
import kotlinx.coroutines.flow.Flow

@Dao
interface kMuzicDao {

    @Query("SELECT * FROM music")
    fun getAllMusic(): Flow<List<MusicDB>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMusic(music: MusicDB)

    @Update
    suspend fun updateMusic(music: MusicDB)

    @Query("SELECT * FROM music WHERE music.id = :muzicId")
    suspend fun getMusicById(muzicId: String): MusicDB

    @Query("SELECT * FROM music WHERE music.is_favorite = 1")
    fun getFavoriteMusic(): Flow<List<MusicDB>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPlaylist(playlistDB: PlaylistDB)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMusicToPlaylist(ref: PlaylistAndMusicRef)

    @Query("DELETE FROM playlist_and_music WHERE playlist_id = \"8664e1aa-6fc1-4801-b9fd-c4858b92da09\"")
    suspend fun deleteAllNowPlaylist()

    @Query("DELETE FROM playlist_and_music WHERE music_id = :musicId")
    fun deleteMusicFromNowPlaylist(musicId: String)

    @Transaction
    @Query("SELECT * FROM playlist WHERE playlist.id = \"8664e1aa-6fc1-4801-b9fd-c4858b92da09\"")
    fun getNowPlaylist(): PlaylistWithMusic
}
