package com.icongkhanh.kmuzic.domain.models

open class Music(
    val id: String,
    val name: String?,
    val authorName: String?,
    val isFavorite: Boolean,
    val path: String
)
