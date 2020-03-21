package com.icongkhanh.kmuzic.data.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist")
data class PlaylistDB(
    @PrimaryKey
    val id: String,
    val name: String
)
