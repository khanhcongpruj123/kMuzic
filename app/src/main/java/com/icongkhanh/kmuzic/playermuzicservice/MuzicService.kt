package com.icongkhanh.kmuzic.playermuzicservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.icongkhanh.kmuzic.MainActivity
import com.icongkhanh.kmuzic.R
import java.util.*

class MuzicService : Service(), OnMuzicStateChangedListener {

    companion object {

        val TAG = "MuzicService"

        val PLAY = "com.icongkhanh.kmuzic.PLAY"
        val PLAY_OR_PAUSE = "com.icongkhanh.kmuzic.PLAY_OR_PAUSE"
        val STOP = "com.icongkhanh.kmuzic.STOP"
        val NEXT = "com.icongkhanh.kmuzic.NEXT"
        val PREVIOUS = "com.icongkhanh.kmuzic.PREVIOUS"
    }

    private lateinit var notificationLayout: RemoteViews
    private val CHANNEL_ID: String = "kMuzic Service"
    val binder = LocalBinder()

    val player = MediaPlayer()
    private val nowPlaylist = NowPlaylist()

    /**
     * when muzic state changed, must notify by calling handleMuzicStateListener()
     * */
    var muzicState: MuzicState = MuzicState.STOP
        set(value) {
            Log.d(TAG, "Set State")
            field = value
            handleMuzicStateListener(field)
        }

    /**
     * schedule task
     * */
    val timer = Timer()

    val progressTask = object : TimerTask() {
        override fun run() {
            val progress = getProgress()
            handleProgressChanged(progress)
        }
    }

    /**
     * check service is running in foreground
     * */
    private var isPlayingForeground = false

    /**
     * check service is running
     * */
    private var isServiceRunning = false

    private var stateMuzicListener = mutableListOf<OnMuzicStateChangedListener>()
    private var muzicPlayingChangedListener = mutableListOf<OnMuzicPlayingChangedListener>()
    private var progressChangedListener = mutableListOf<OnProgressChangedListener>()
    private var nowPlayListChangedListener = mutableListOf<OnNowPlayListChangedListener>()

    init {

        this.addOnStateMuzicChanged(this)
    }

    override fun onCreate() {
        super.onCreate()

        timer.schedule(progressTask, 0, 500)

        muzicState = MuzicState.STOP

        isServiceRunning = true
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        createNotificationChannel()

        intent?.let {
            when (it.action) {
                PLAY_OR_PAUSE -> {
                    playOrPause()
                }
                STOP -> {
                    stop()
                }
                NEXT -> {
                    next()
                }
                PREVIOUS -> {
                    previous()
                }
            }
        }

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
        player.let { p ->

            getCurrentMusic()?.path?.let {
                p.setDataSource(it)
                p.prepare()
                p.start()

                muzicState = MuzicState.PLAY
                handleMuzicPlayingChangedListener(nowPlaylist.getCurrentMuzic())

                player.setOnCompletionListener {
                    next()
                }
            }
        }
    }

    fun handleMuzicPlayingChangedListener(currentMuzic: Muzic?) {
        muzicPlayingChangedListener.forEach { callback ->
            currentMuzic?.let { callback.onChanged(it) }
        }
    }

    private fun handleProgressChanged(progress: Float) {
        progressChangedListener.forEach {
            it.onChanged(progress)
        }
    }

    fun isExistedMuzic(muzic: Muzic): Boolean {
        return nowPlaylist.isExistedMusic(muzic)
    }

    fun getCurrentMusic() = nowPlaylist.getCurrentMuzic()

    fun pause() {
        if (player.isPlaying) {
            player.pause()
            muzicState = MuzicState.PAUSE
        }
    }

    fun stop() {
        player.stop()
        timer.cancel()

        stopForeground(true)
        muzicState = MuzicState.STOP
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
        handleNowPlaylistChanged(nowPlaylist.listMuzic)
    }

    fun addMusicToPlaylistAndPlay(muzic: Muzic) {

        if (nowPlaylist.isExistedMusic(muzic)) {
            val index = nowPlaylist.indexOfMuzic(muzic)
            if (index == nowPlaylist.currentPosition) {
                if (isPause()) resume()
                else play()
            } else {
                nowPlaylist.currentPosition = index
                play()
            }
        } else {
            nowPlaylist.addMusicAndPlay(muzic)
            handleNowPlaylistChanged(nowPlaylist.listMuzic)
            play()
        }
    }

    //TODO: fix logic play or pause
    fun playOrPause() {
        when {
            isPlaying() -> {
                pause()
            }
            isPause() -> {
                resume()
            }
            isStopped() -> {
                play()
            }
        }
    }

    private fun resume() {
        player.start()
        muzicState = MuzicState.PLAY
    }

    fun isPause(): Boolean {
        return muzicState == MuzicState.PAUSE
    }

    fun isPlaying(): Boolean {
        return muzicState == MuzicState.PLAY
    }

    fun isStopped() = muzicState == MuzicState.STOP

    fun buildNotification(state: MuzicState): Notification {

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        notificationLayout = RemoteViews(packageName, R.layout.mini_controller_notifi)

        val playIntent = Intent(this, MuzicService::class.java).let {
            it.action = PLAY_OR_PAUSE
            PendingIntent.getService(this, 1, it, 0)
        }

        val stopIntent = Intent(this, MuzicService::class.java).let {
            it.action = STOP
            PendingIntent.getService(this, 1, it, 0)
        }

        val nextIntent = Intent(this, MuzicService::class.java).let {
            it.action = NEXT
            PendingIntent.getService(this, 1, it, 0)
        }

        val previousIntent = Intent(this, MuzicService::class.java).let {
            it.action = PREVIOUS
            PendingIntent.getService(this, 1, it, 0)
        }

        notificationLayout.setTextViewText(R.id.tv_name, nowPlaylist.getCurrentMuzic()?.name)
        notificationLayout.setOnClickPendingIntent(R.id.btn_play_or_pause, playIntent)
        notificationLayout.setOnClickPendingIntent(R.id.btn_stop, stopIntent)
        notificationLayout.setOnClickPendingIntent(R.id.btn_next, nextIntent)
        notificationLayout.setOnClickPendingIntent(R.id.btn_previous, previousIntent)

        when (state) {
            MuzicState.PLAY -> {
                notificationLayout.setImageViewResource(R.id.btn_play_or_pause, R.drawable.ic_pause)
            }
            MuzicState.STOP, MuzicState.PAUSE -> {
                notificationLayout.setImageViewResource(
                    R.id.btn_play_or_pause,
                    R.drawable.ic_play_arrow
                )
            }
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_play_arrow)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .build()
    }

    /**
     * above android O, must create notification channel before create notification
     * */
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "kMuzic"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun addOnStateMuzicChanged(listener: OnMuzicStateChangedListener) {
        this.stateMuzicListener.add(listener)
    }

    fun addOnStateMuzicChanged(listeners: List<OnMuzicStateChangedListener>) {
        this.stateMuzicListener.addAll(listeners)
    }

    fun addOnProgressChanged(listener: OnProgressChangedListener) {
        this.progressChangedListener.add(listener)
    }

    fun addOnProgressChanged(listeners: List<OnProgressChangedListener>) {
        this.progressChangedListener.addAll(listeners)
    }

    fun addOnMuzicPlayingChanged(listener: OnMuzicPlayingChangedListener) {
        this.muzicPlayingChangedListener.add(listener)
    }

    fun addOnMuzicPlayingChanged(listeners: List<OnMuzicPlayingChangedListener>) {
        this.muzicPlayingChangedListener.addAll(listeners)
    }

    /**
     * handle all callback in list listener
     * */
    fun handleMuzicStateListener(state: MuzicState) {
        for (it in stateMuzicListener) {
            it.onChanged(state)
        }
    }

    fun handleNowPlaylistChanged(list: List<Muzic>) {
        nowPlayListChangedListener.forEach {
            it.onChanged(list)
        }
    }

    override fun onDestroy() {

        isServiceRunning = false
        super.onDestroy()
    }

    fun getProgress(): Float {
        return player.currentPosition.toFloat() / player.duration
    }

    override fun onChanged(state: MuzicState) {
        // when service stop, state is idle, notification will be removed, if startForeground, notification will be created, it is fail logic
        if (state == MuzicState.STOP) return
        if (isPlayingForeground) startForeground(1, buildNotification(state))
    }

    fun addOnNowPlaylistChanged(listeners: List<OnNowPlayListChangedListener>) {
        this.nowPlayListChangedListener.addAll(listeners)
    }

    fun addOnNowPlaylistChanged(listener: OnNowPlayListChangedListener) {
        this.nowPlayListChangedListener.add(listener)
    }

    fun runInForeground() {
        if (isPlayingForeground) return
        if (muzicState == MuzicState.STOP) return
        Log.d(TAG, "Run In foreground")
        startForeground(1, buildNotification(muzicState))
        isPlayingForeground = true
    }

    fun stopRunInForeground() {
        if (!isPlayingForeground) return
        Log.d(TAG, "Stop foreground")
        stopForeground(true)
        isPlayingForeground = false
    }

    fun initNowPlaylist() {
        if (nowPlaylist.listMuzic.isEmpty()) nowPlaylist.reset()
        else if (nowPlaylist.currentPosition == -1) {
            nowPlaylist.goToFirst()
        }
        handleMuzicPlayingChangedListener(getCurrentMusic())
    }

    fun getListMusic() = nowPlaylist.listMuzic
}
