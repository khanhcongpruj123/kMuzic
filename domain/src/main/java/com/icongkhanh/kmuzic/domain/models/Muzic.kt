package com.icongkhanh.kmuzic.domain.models

data class Muzic(
    val id: String,
    val name: String,
    val authorName: String,
    val isFavorite: Boolean,
    val path: String
)