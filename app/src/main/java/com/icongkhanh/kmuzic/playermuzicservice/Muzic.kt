package com.icongkhanh.kmuzic.playermuzicservice

import com.icongkhanh.kmuzic.domain.models.Muzic
import java.io.Serializable


data class Muzic(
    val id: String,
    val name: String?,
    val author: String?,
    val isFavorite: Boolean,
    val path: String): Serializable {

    fun toDomainModel() : Muzic {
        return Muzic(
            id,
            name,
            author,
            isFavorite,
            path
        )
    }
}