package com.icongkhanh.kmuzic.playmuzicservice

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MuzicService : Service() {

    companion object {
        val PLAY = "com.icongkhanh.kmuzic.PLAY"
    }

    val binder = LocalBinder()

    val player = MediaPlayer()
    val nowPlaylist = NowPlaylist()


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    inner class LocalBinder : Binder() {

        fun getService(): MuzicService {
            return this@MuzicService
        }
    }

    fun play() {
        if (player.isPlaying) player.stop()
        player.reset()
        player.let {
            it.setDataSource(nowPlaylist.getPlayingMuzic().path)
            it.prepare()
            it.start()
        }
    }

    fun isExistedMuzic(muzic: Muzic) : Boolean {
        return nowPlaylist.isExistedMuzic(muzic)
    }

    fun pause() {
        if (player.isPlaying) player.pause()
    }

    fun stop() {
        player.stop()
    }

    fun next() {
        nowPlaylist.next()
        play()
    }

    fun previous() {
        nowPlaylist.previous()
        play()
    }

    fun addMusicToPlaylist(muzic: Muzic) {
        nowPlaylist.addMusic(muzic)
    }

    fun addMusicToPlaylistAndPlay(muzic: Muzic) {

        var index = -1

        if (isExistedMuzic(muzic)) {
            index = nowPlaylist.indexOfMuzic(muzic)
            nowPlaylist.currentPosition = index
        } else {
            index = nowPlaylist.addMusic(muzic)
            nowPlaylist.currentPosition = index
            play()
        }
    }

    fun playOrPause() {
        if (player.isPlaying) player.pause()
        else {
            if (nowPlaylist.currentPosition == -1) return
            else {
                player.start()
            }
        }
    }

    fun isPlaying(): Boolean {
        return player?.isPlaying
    }
}