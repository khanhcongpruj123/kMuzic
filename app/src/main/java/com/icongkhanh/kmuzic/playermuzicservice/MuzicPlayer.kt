package com.icongkhanh.kmuzic.playermuzicservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import com.icongkhanh.kmuzic.domain.Result
import com.icongkhanh.kmuzic.domain.isSuccess
import com.icongkhanh.kmuzic.domain.usecases.GetNowPlaylistUsecase
import com.icongkhanh.kmuzic.utils.mapToServiceModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MuzicPlayer(val context: Context, val getNowPlaylist: GetNowPlaylistUsecase) {

    val handlerThread = HandlerThread("player")
    val handler : Handler

    init {
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    /**
     * state of bind
     * */
    var isBind = false

    /**
     * it play music in background
     * */
    var muzicService: MuzicService? = null

    /**
     * this job is load all music in nowplaylist which is saved when exit app,
     * start job when bind with serive and cancel when unbind
     * */
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

    /**
     * notify when state music (play, pause, stop)
     * */
    private val stateMuzicListener = mutableListOf<OnMuzicStateChangedListener>()

    /**
     * notify when current music is changed
     * */
    private val muzicPlayingListener = mutableListOf<OnMuzicPlayingChangedListener>()

    /**
     * notify when progress of playing music is changed
     * */
    private val progressChangedListener = mutableListOf<OnProgressChangedListener>()

    /**
     * notify when list of music in nowplaylist is changed
     * */
    private val nowPlayListChangedListener = mutableListOf<OnNowPlayListChangedListener>()

    /**
     * call when main activity is launched, player start music service and bind with it
     * */
    fun bind() {
        val intent = Intent(context, MuzicService::class.java)
        context.startService(intent)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    /**
     * call when main activity is stop, music service will run in foreground with notification,
     * load nowplaylist will by stopped
     * and player will unbind service
     * */
    fun unbind() {
        muzicService?.runInForeground()
        loadNowpLaylistJob.cancel()
        context.unbindService(connection)
    }

    /**
     * call when player is binded with service,
     * set listener for muzic service, and load now playlist for service
     * */
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

        handler.postDelayed({
            if (!isValidate()) {
                bind()
            }
            if (getCurrentMuzic()?.id != muzic.id) {
                muzicService?.addMusicToPlaylistAndPlay(muzic)
            }
        }, 100)
    }

    fun playOrPause() {
        handler.postDelayed({
            if (!isValidate()) {
                bind()
            }
            muzicService?.playOrPause()
        }, 100)
    }

    fun pause() {
        handler.postDelayed({
            if (!isValidate()) bind()
            muzicService?.pause()
        }, 100)
    }

    fun next() {
        handler.postDelayed({
            if (!isValidate()) {
                bind()
            }
            muzicService?.next()
        }, 100)
    }

    fun previous() {
        handler.postDelayed({
            if (!isValidate()) {
                bind()
            }
            muzicService?.previous()
        }, 100)
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
            var list: List<Muzic> = emptyList()
            getNowPlaylist().onEach { result ->
//                Log.d(TAG, result.toString())
                when (result) {
                    is Result.Success -> {
                        if (result.isSuccess()) list = result.data.map { it.mapToServiceModel() }
                    }
                }
            }.collect()
            list.forEach {
                muzicService?.addMusicToPlaylist(it)
            }
            muzicService?.initNowPlaylist()
        }
    }

    companion object {
        val TAG = "MuzicPlayer"
    }
}
