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
    private val connection = object : ServiceConnection {
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
    private val stateMuzicListener =  arrayListOf<OnMuzicStateChangedListener>()
    private val muzicPlayingListener = arrayListOf<OnMuzicPlayingChangedListener>()

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
        muzicService?.let {
            it.addOnStateMuzicChanged {state ->
                muzicState = state

                for (it in stateMuzicListener) {
                    it.onChanged(state)
                }
            }
            it.addOnMuzicPlayingChanged { muzic ->
                for (it in muzicPlayingListener) {
                    it.onChanged(muzic)
                }
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

    fun getListMusic() = muzicService?.nowPlaylist?.listMuzic


    fun addOnStateChangedListener(listener: OnMuzicStateChangedListener) {
        this.stateMuzicListener.add(listener)
    }

    fun addOnMuzicPlayingChangedListener(listener: OnMuzicPlayingChangedListener) {
        this.muzicPlayingListener.add(listener)
    }

    fun getProgress() = muzicService?.getProgress()

    fun getCurrentMuzic() = muzicService?.nowPlaylist?.getCurrentMuzic()

    fun unsubscribe(any: Any) {
        if (any is OnMuzicPlayingChangedListener) muzicPlayingListener.remove(any)
        if (any is OnMuzicStateChangedListener) stateMuzicListener.remove(any)
    }
}