package com.icongkhanh.kmuzic.data.local.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlaylistWithMusic(
    @Embedded
    val playlistDB: PlaylistDB,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = PlaylistAndMusicRef::class,
            parentColumn = "playlist_id",
            entityColumn = "music_id"
        )
    )
    val listMusic: List<MusicDB>
)
