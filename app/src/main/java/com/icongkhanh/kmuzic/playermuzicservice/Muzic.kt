package com.icongkhanh.kmuzic.playmuzicservice

import java.io.Serializable


data class Muzic(
    val id: String,
    val name: String,
    val author: String,
    val isFavorite: Boolean,
    val path: String): Serializable