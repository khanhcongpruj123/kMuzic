package com.icongkhanh.kmuzic.playermuzicservice

import android.app.*
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

class MuzicService : Service() {

    companion object {

        val TAG = this::class.java.simpleName

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

    private var stateMuzicListener = mutableListOf<OnMuzicStateChangedListener>()
    private var muzicPlayingChangedListener = mutableListOf<OnMuzicPlayingChangedListener>()

    init {
        player.setOnCompletionListener {
            next()
        }

        this.addOnStateMuzicChanged {
            Log.d("Muzic Service", "Muzic State: ${it}")

            if (it != MuzicState.IDLE) {
                startForeground(1, buildNotification(it))
            }
        }

    }


    override fun onCreate() {
        super.onCreate()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        createNotificationChannel()

        intent?.let {
            when(it.action) {
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
        player.let {
            Log.d(TAG, "Playing Muzic: ${nowPlaylist.getCurrentMuzic()?.name}")
            it.setDataSource(nowPlaylist.getCurrentMuzic()?.path)
            it.prepare()
            it.start()

            handleMuzicStateListener(MuzicState.PLAY)
            handleMuzicPlayingChangedListener(nowPlaylist.getCurrentMuzic())
        }

    }

    private fun handleMuzicPlayingChangedListener(currentMuzic: Muzic?) {
        muzicPlayingChangedListener.forEach { callback ->
            currentMuzic?.let { callback.onChanged(it) }
        }
    }

    fun isExistedMuzic(muzic: Muzic) : Boolean {
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
        stopForeground(true)
        handleMuzicStateListener(MuzicState.IDLE)
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
        }
        else {
            if (nowPlaylist.currentPosition == -1) {
                handleMuzicStateListener(MuzicState.IDLE)
            }
            else {
                player.start()
                handleMuzicStateListener(MuzicState.PLAY)
            }
        }
    }

    fun isPlaying(): Boolean {
        return player?.isPlaying
    }

    fun buildNotification(state: MuzicState): Notification {

//        Log.d("AppLog", "Build Notification")

        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
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

        when(state) {
            MuzicState.PLAY -> {
                notificationLayout.setImageViewResource(R.id.btn_play_or_pause, R.drawable.ic_pause)
            }
            MuzicState.IDLE, MuzicState.PAUSE -> {
                notificationLayout.setImageViewResource(R.id.btn_play_or_pause, R.drawable.ic_play_arrow)
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

    fun addOnStateMuzicChanged(listener: (state: MuzicState) -> Unit) {
        this.stateMuzicListener.add(object : OnMuzicStateChangedListener {
            override fun onChanged(state: MuzicState) {
                listener(state)
            }
        })
    }

    fun addOnMuzicPlayingChanged(listener: (muzic: Muzic) -> Unit) {
        this.muzicPlayingChangedListener.add(object : OnMuzicPlayingChangedListener {
            override fun onChanged(muzic: Muzic) {
                listener(muzic)
            }
        })
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
        stop()
    }

    fun getProgress(): Float {
        return player.currentPosition.toFloat() / player.duration
    }


}