package com.icongkhanh.kmuzic.playermuzicservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
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
    val listener =  arrayListOf<OnMuzicStateChangedListener>()

    fun bind() {
        val intent = Intent(context, MuzicService::class.java)
        context.startService(intent)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbind() {
        context.unbindService(connection)
    }

    fun onBind() {
        isBind = true
        muzicService?.addOnStateMuzicChanged {state ->
            muzicState = state

            for (it in listener) {
                it.onChanged(state)
            }
        }
    }

    fun onUnbind() {
        isBind = false
    }

    fun play(muzic: Muzic) {

        if (!isValidate()) {
            bind()
        }
        muzicService?.addMusicToPlaylistAndPlay(muzic)
    }

    fun playOrPause() {
        if (!isValidate()) {
            bind()
        }
        muzicService?.playOrPause()

    }

    fun pause() {
        if (!isValidate()) return
        muzicService?.pause()
    }

    fun next() {
        if (!isValidate()) {
            bind()
        }
        muzicService?.next()
    }

    fun previous() {
        if (!isValidate()) {
            bind()
        }
        muzicService?.previous()
    }

    fun isValidate() : Boolean {
        return isBind || muzicService != null
    }


    fun addOnStateChangedListener(listener: OnMuzicStateChangedListener) {
        this.listener.add(listener)
    }
}