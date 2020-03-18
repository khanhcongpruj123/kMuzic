package com.icongkhanh.kmuzic.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.icongkhanh.kmuzic.data.local.database.model.MusicDB
import kotlinx.coroutines.flow.Flow

@Dao
interface kMuzicDao {

    @Query("SELECT * FROM music")
    fun getAllMusic(): Flow<List<MusicDB>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMusic(music: MusicDB)

    @Update
    fun updateMusic(music: MusicDB)

    @Query("SELECT * FROM music WHERE music.id = :muzicId")
    fun getMusicById(muzicId: String): MusicDB
}
