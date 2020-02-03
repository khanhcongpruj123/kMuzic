package com.icongkhanh.kmuzic.playmuzicservice

import android.media.MediaPlayer
import android.net.Uri

class MuzicPlayer {

    val nowPlaylist: NowPlaylist
    val player: MediaPlayer

    init {
        nowPlaylist = NowPlaylist()
        player = MediaPlayer()
    }

    fun play() {
        player.apply {

            if (player.isPlaying) {
                player.stop()
            }

            player.setDataSource(nowPlaylist.getPlayingMuzic().path)
            player.prepare()
            player.start()
        }
    }

    fun pause() {
        player.pause()
    }

    fun stop() {
        player.stop()
    }

    fun next() {
        nowPlaylist.next()
        play()
    }

    fun addMusicToPlaylist(muzic: Muzic) {
        nowPlaylist.addMusic(muzic)
    }

    fun addMusicToPlaylistAndPlay(muzic: Muzic) {
        val index = nowPlaylist.addMusic(muzic)
        nowPlaylist.currentPosition = index
        play()
    }
}