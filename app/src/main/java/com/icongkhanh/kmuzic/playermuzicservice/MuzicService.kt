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
    val nowPlaylist = NowPlaylist()
    var muzicState = MuzicState.IDLE
    val timer = Timer()
    val progressTask = object : TimerTask() {
        override fun run() {
            val progress = getProgress()
//            Log.d(TAG,"progress: ${progress}")
            handleProgressChanged(progress)
        }
    }

    private var stateMuzicListener = mutableListOf<OnMuzicStateChangedListener>()
    private var muzicPlayingChangedListener = mutableListOf<OnMuzicPlayingChangedListener>()
    private var progressChangedListener = mutableListOf<OnProgressChangedListener>()

    init {
        player.setOnCompletionListener {
            next()
        }

        this.addOnStateMuzicChanged(this)
    }

    override fun onCreate() {
        super.onCreate()

        timer.schedule(progressTask, 0, 500)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        /**
         * start get progress task
         * */

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

        startForeground(1, buildNotification(MuzicState.PLAY))

        if (player.isPlaying) player.stop()
        player.reset()
        player.let { p ->
//            Log.d(TAG, "Playing Muzic: ${nowPlaylist.getCurrentMuzic()?.name}")
            nowPlaylist.getCurrentMuzic()?.path?.let {
                p.setDataSource(it)
                p.prepare()
                p.start()

                handleMuzicStateListener(MuzicState.PLAY)
                handleMuzicPlayingChangedListener(nowPlaylist.getCurrentMuzic())
            }
        }
    }

    private fun handleMuzicPlayingChangedListener(currentMuzic: Muzic?) {
        muzicPlayingChangedListener.forEach { callback ->
            currentMuzic?.let { callback.onChanged(it) }
        }
    }

    private fun handleProgressChanged(progress: Float) {
//        Log.d(TAG, "handle listener: ${progressChangedListener.size}")
        progressChangedListener.forEach {
            it.onChanged(progress)
        }
    }

    fun isExistedMuzic(muzic: Muzic): Boolean {
        return nowPlaylist.isExistedMuzic(muzic)
    }

    fun pause() {
        if (player.isPlaying) {
            player.pause()
            handleMuzicStateListener(MuzicState.PAUSE)
        }
    }

    fun stop() {
        player.stop()
        timer.cancel()

        stopForeground(true)
        handleMuzicStateListener(MuzicState.IDLE)
//        stopSelf()
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

        if (nowPlaylist.isExistedMuzic(muzic)) {
            val index = nowPlaylist.indexOfMuzic(muzic)
            if (index == nowPlaylist.currentPosition) {
                if (player.isPlaying) {
                } else {
                    if (muzicState == MuzicState.PAUSE) player.start()
                    else play()
                }
            } else {
                nowPlaylist.currentPosition = index
                play()
            }
        } else {
            nowPlaylist.addMusicAndPlay(muzic)
            play()
        }
    }

    fun playOrPause() {
        if (player.isPlaying) {
            player.pause()
            handleMuzicStateListener(MuzicState.PAUSE)
        } else {
            if (nowPlaylist.currentPosition == -1) {
                handleMuzicStateListener(MuzicState.IDLE)
            } else {
                player.start()
                handleMuzicStateListener(MuzicState.PLAY)
            }
        }
    }

    fun isPlaying(): Boolean {
        return player?.isPlaying
    }

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
            MuzicState.IDLE, MuzicState.PAUSE -> {
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
        muzicState = state
        for (it in stateMuzicListener) {
            it.onChanged(state)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun getProgress(): Float {
        return player.currentPosition.toFloat() / player.duration
    }

    override fun onChanged(state: MuzicState) {
        // when service stop, state is idle, notification will be removed, if startForeground, notification will be created, it is fail logic
        if (state == MuzicState.IDLE) return
        startForeground(1, buildNotification(state))
    }
}
