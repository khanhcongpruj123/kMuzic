package com.icongkhanh.kmuzic.data.utils

import com.icongkhanh.kmuzic.data.local.database.model.MusicDB
import com.icongkhanh.kmuzic.domain.models.Music

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
