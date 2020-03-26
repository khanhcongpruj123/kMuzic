package com.icongkhanh.kmuzic.fragments.home.homeviewpager.allmusic

import com.icongkhanh.kmuzic.domain.models.Music

interface AllMusicContract {
    data class ViewState(
        val music: List<Music>,
        val isLoading: Boolean,
        val error: Throwable?
    ) {
        companion object {
            fun initial() = ViewState(
                music = emptyList(),
                isLoading = true,
                error = null
            )
        }
    }

    sealed class AllMusicViewEvent {
        data class ClickItemMusic(val music: Music) : AllMusicViewEvent()
        data class LongClickItemMusic(val music: Music) : AllMusicViewEvent()
        object DismissBottomMenu : AllMusicViewEvent()
        object Back : AllMusicViewEvent()
    }
}
