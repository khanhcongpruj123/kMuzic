package com.icongkhanh.kmuzic.uimodels

import com.icongkhanh.kmuzic.domain.models.Music

data class ListMusicUiModel(
    val list: List<Music> = emptyList(),
    val isLoading: Boolean = false
)
