package com.icongkhanh.kmuzic.playermuzicservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.icongkhanh.kmuzic.domain.usecases.GetNowPlaylistUsecase
import com.icongkhanh.kmuzic.utils.mapToServiceModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MuzicPlayer(val context: Context, val getNowPlaylist: GetNowPlaylistUsecase) {

    var isBind = false
    var muzicService: MuzicService? = null
    lateinit var loadNowpLaylistJob: Job
    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "Service unconnected!")
            muzicService = null
            onUnbind()
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "Service connected!")
            val binder = service as MuzicService.LocalBinder
            muzicService = binder.getService()
            onBind()
        }
    }

    private val stateMuzicListener = mutableListOf<OnMuzicStateChangedListener>()
    private val muzicPlayingListener = mutableListOf<OnMuzicPlayingChangedListener>()
    private val progressChangedListener = mutableListOf<OnProgressChangedListener>()
    private val nowPlayListChangedListener = mutableListOf<OnNowPlayListChangedListener>()

    fun bind() {
        val intent = Intent(context, MuzicService::class.java)
        context.startService(intent)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbind() {
        muzicService?.runInForeground()
        loadNowpLaylistJob.cancel()
        context.unbindService(connection)
    }

    fun onBind() {
        Log.d(TAG, "onBind")
        isBind = true
        muzicService?.let {
            it.addOnStateMuzicChanged(stateMuzicListener)
            it.addOnMuzicPlayingChanged(muzicPlayingListener)
            it.addOnProgressChanged(progressChangedListener)
            it.addOnNowPlaylistChanged(nowPlayListChangedListener)
        }

        muzicService?.stopRunInForeground()

        loadNowPlaylist()
    }

    fun onUnbind() {
        isBind = false
    }

    fun play(muzic: Muzic) {

        if (!isValidate()) {
            bind()
        }
        if (getCurrentMuzic()?.id == muzic.id) return
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

    fun isValidate(): Boolean {
        return isBind || muzicService != null
    }

    fun getListMusic() = muzicService?.getListMusic()

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

    fun addOnNowplaylistChangedListener(listener: OnNowPlayListChangedListener) {
        this.nowPlayListChangedListener.add(listener)
        muzicService?.addOnNowPlaylistChanged(listener)
    }

    fun getProgress() = muzicService?.getProgress()

    fun getCurrentMuzic() = muzicService?.getCurrentMusic()

    fun unsubscribe(any: Any) {
        if (any is OnMuzicPlayingChangedListener) muzicPlayingListener.remove(any)
        if (any is OnMuzicStateChangedListener) stateMuzicListener.remove(any)
        if (any is OnProgressChangedListener) progressChangedListener.remove(any)
        if (any is OnNowPlayListChangedListener) nowPlayListChangedListener.remove(any)
    }

    fun getMusicState(): MuzicState? {
        return muzicService?.muzicState
    }

    fun loadNowPlaylist() {
        loadNowpLaylistJob = CoroutineScope(Dispatchers.IO).launch {
            val list = getNowPlaylist()
            Log.d(TAG, "list: $list")
            list.forEach {
                Log.d(TAG, "item: ${it.name}")
                muzicService?.addMusicToPlaylist(it.mapToServiceModel())
            }
            muzicService?.resetNowPlaylist()
        }
    }

    companion object {
        val TAG = "MuzicPlayer"
    }
}
