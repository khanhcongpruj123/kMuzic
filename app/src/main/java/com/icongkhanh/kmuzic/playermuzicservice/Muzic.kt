package com.icongkhanh.kmuzic.playermuzicservice

import com.icongkhanh.kmuzic.domain.models.Music
import java.io.Serializable


data class Muzic(
    val id: String,
    val name: String?,
    val author: String?,
    val isFavorite: Boolean,
    val path: String): Serializable {

    fun toDomainModel(): Music {
        return Music(
            id,
            name,
            author,
            isFavorite,
            path
        )
    }
}