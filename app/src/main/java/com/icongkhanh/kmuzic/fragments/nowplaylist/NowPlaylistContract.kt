package com.icongkhanh.kmuzic.fragments.nowplaylist

interface NowPlaylistContract {

    sealed class NowPlaylistViewEvent() {

        object Next : NowPlaylistViewEvent()
        object Previous : NowPlaylistViewEvent()
        object PlayOrPause : NowPlaylistViewEvent()
        object ToggleFavorite : NowPlaylistViewEvent()
        object Back : NowPlaylistViewEvent()
    }
}
