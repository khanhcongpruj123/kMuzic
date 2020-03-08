package com.icongkhanh.kmuzic.fragments.homeviewpager.allmusic

import com.icongkhanh.kmuzic.domain.models.Muzic

interface AllMusicContract {
    data class ViewState(
        val musics: List<Muzic>,
        val isLoading: Boolean,
        val error: Throwable?
    ) {
        companion object {
            fun initial() = ViewState(
                musics = emptyList(),
                isLoading = true,
                error = null
            )
        }
    }
}