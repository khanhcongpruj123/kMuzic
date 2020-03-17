package com.icongkhanh.kmuzic.playermuzicservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

class MuzicPlayer(val context: Context) {

    var isBind = false
    var muzicService: MuzicService? = null
    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
//            Log.d("AppLog", "Service connected!")
            muzicService = null
            onUnbind()
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            Log.d("AppLog", "Service connected!")
            val binder = service as MuzicService.LocalBinder
            muzicService = binder.getService()
            onBind()
        }

    }
    var muzicState = MuzicState.IDLE
    private val stateMuzicListener = mutableListOf<OnMuzicStateChangedListener>()
    private val muzicPlayingListener = mutableListOf<OnMuzicPlayingChangedListener>()
    private val progressChangedListener = mutableListOf<OnProgressChangedListener>()

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
//        Log.d("Player", "init")
        muzicService?.let {
            it.addOnStateMuzicChanged(stateMuzicListener)
            it.addOnMuzicPlayingChanged(muzicPlayingListener)
            it.addOnProgressChanged(progressChangedListener)
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
//        Log.d("Player", "add state listener")
        this.stateMuzicListener.add(listener)
        muzicService?.addOnStateMuzicChanged(listener)
    }

    fun addOnMuzicPlayingChangedListener(listener: OnMuzicPlayingChangedListener) {
//        Log.d("Player", "add music playing listener")
        this.muzicPlayingListener.add(listener)
        muzicService?.addOnMuzicPlayingChanged(listener)
    }

    fun addOnProgressChangedListener(listener: OnProgressChangedListener) {
//        Log.d("Player", "add on progress listener")
        this.progressChangedListener.add(listener)
        muzicService?.addOnProgressChanged(listener)
    }

    fun getProgress() = muzicService?.getProgress()

    fun getCurrentMuzic() = muzicService?.nowPlaylist?.getCurrentMuzic()

    fun unsubscribe(any: Any) {
        if (any is OnMuzicPlayingChangedListener) muzicPlayingListener.remove(any)
        if (any is OnMuzicStateChangedListener) stateMuzicListener.remove(any)
        if (any is OnProgressChangedListener) progressChangedListener.remove(any)
    }
}