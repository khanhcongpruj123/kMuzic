package com.icongkhanh.kmuzic.utils

import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.playermuzicservice.Muzic

fun Music.mapToServiceModel() = Muzic(
    this.id,
    this.name,
    this.authorName,
    this.isFavorite,
    this.path
)

fun Muzic.mapToDomainModel() = Music(
    this.id,
    this.name,
    this.author,
    this.isFavorite,
    this.path
)
