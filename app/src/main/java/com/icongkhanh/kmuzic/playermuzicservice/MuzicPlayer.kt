package com.icongkhanh.kmuzic.playmuzicservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log

class MuzicPlayer(val context: Context) {

    var isBind = false
    var muzicService: MuzicService? = null
    val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("AppLog", "Service connected!")
            muzicService = null
            onUnbind()
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("AppLog", "Service connected!")
            val binder = service as MuzicService.LocalBinder
            muzicService = binder.getService()
            onBind()
        }

    }
    var muzicState = MuzicState.IDLE
    lateinit var listener: OnMuzicStateChangedListener

    fun bind() {
        if (isBind) return
        val intent = Intent(context, MuzicService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbind() {
        context.unbindService(connection)
    }

    fun onBind() {
        isBind = true
        if (muzicService!!.isPlaying()) changeState(MuzicState.PLAY)
    }

    fun onUnbind() {
        isBind = false
    }

    fun play(muzic: Muzic) {
        if (!isValidate()) return
        muzicService?.addMusicToPlaylistAndPlay(muzic)
        changeState(MuzicState.PLAY)
    }

    fun playOrPause() {
        if (!isValidate()) return
        muzicService?.playOrPause()
        val isPlaying = muzicService?.isPlaying()!! || false
        if (isPlaying) changeState(MuzicState.PLAY)
        else changeState(MuzicState.PAUSE)

    }

    fun pause() {
        if (!isValidate()) return
        muzicService?.pause()
    }

    fun next() {
        if (!isValidate()) return
        muzicService?.next()
    }

    fun previous() {
        if (!isValidate()) return
        muzicService?.previous()
    }

    fun isValidate() : Boolean {
        return isBind || muzicService != null
    }

    fun changeState(state: MuzicState) {
        muzicState = state
        listener?.onChanged(muzicState)
    }

    fun setOnStateChangedListener(listener: OnMuzicStateChangedListener) {
        this.listener = listener
    }
}