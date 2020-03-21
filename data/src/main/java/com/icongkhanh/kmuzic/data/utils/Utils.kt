package com.icongkhanh.kmuzic.data.utils

import com.icongkhanh.kmuzic.data.local.database.model.MusicDB
import com.icongkhanh.kmuzic.data.local.database.model.PlaylistDB
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.domain.models.Playlist

fun Music.mapToDBModel() = MusicDB(
    this.id,
    this.name,
    this.authorName,
    this.isFavorite,
    this.path
)

fun MusicDB.mapToDomainModel() = Music(
    this.id,
    this.name,
    this.authorName,
    this.isFavorite,
    this.path
)

fun Playlist.mapToDBModel() = PlaylistDB(
    this.id,
    this.name
)
