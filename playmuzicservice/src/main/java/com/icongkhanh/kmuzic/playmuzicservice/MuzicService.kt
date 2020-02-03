package com.icongkhanh.kmuzic.playmuzicservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class MuzicService : Service() {

    companion object {
        val PLAY = "com.icongkhanh.kmuzic.PLAY"
    }

    val binder = LocalBinder()
    val player: MuzicPlayer

    init {
        player = MuzicPlayer()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            val action = intent.action

            when(action) {
                PLAY -> {
                    val muzic = intent.getSerializableExtra("muzic") as Muzic
                    player.addMusicToPlaylistAndPlay(muzic)
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    inner class LocalBinder: Binder() {

        fun getService() : MuzicService{
            return this@MuzicService
        }
    }
}