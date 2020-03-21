package com.icongkhanh.kmuzic.data.local.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "playlist_and_music", primaryKeys = ["playlist_id", "music_id"])
data class PlaylistAndMusicRef(

    @ColumnInfo(name = "playlist_id")
    val playlistId: String,
    @ColumnInfo(name = "music_id")
    val musicId: String
)
