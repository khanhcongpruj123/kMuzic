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
    var playingIndex = -1

    var listener = mutableListOf<OnMuzicStateChangedListener>()

    init {
        player.setOnCompletionListener {
            next()
        }

        this.addOnStateMuzicChanged {
            Log.d("Muzic Service", "Muzic State: ${it}")
            if (it == MuzicState.PLAY) notificationLayout.setImageViewResource(R.id.btn_play_or_pause, R.drawable.ic_pause)
            else notificationLayout.setImageViewResource(R.id.btn_play_or_pause, R.drawable.ic_play_arrow)
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

        startForeground(1, buildNotification())

        if (player.isPlaying) player.stop()
        player.reset()
        player.let {
            it.setDataSource(nowPlaylist.getPlayingMuzic().path)
            it.prepare()
            it.start()

            handleListener(MuzicState.PLAY)
        }

    }

    fun isExistedMuzic(muzic: Muzic) : Boolean {
        return nowPlaylist.isExistedMuzic(muzic)
    }

    fun pause() {
        if (player.isPlaying) {
            player.pause()

            handleListener(MuzicState.PAUSE)
        }
    }

    fun stop() {
        player.stop()
        stopForeground(true)

        handleListener(MuzicState.IDLE)
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

        var index = -1

        if (isExistedMuzic(muzic)) {
            index = nowPlaylist.indexOfMuzic(muzic)
            if (nowPlaylist.currentPosition != index) {
                nowPlaylist.currentPosition = index
                play()
            }
        } else {
            index = nowPlaylist.addMusic(muzic)
            nowPlaylist.currentPosition = index
            play()
        }
    }

    fun playOrPause() {
        if (player.isPlaying) {
            player.pause()
            handleListener(MuzicState.PAUSE)
        }
        else {
            if (nowPlaylist.currentPosition == -1) {
                handleListener(MuzicState.IDLE)
            }
            else {
                player.start()
                handleListener(MuzicState.PLAY)
            }
        }
    }

    fun isPlaying(): Boolean {
        return player?.isPlaying
    }

    fun buildNotification(): Notification {

        Log.d("AppLog", "Build Notification")

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

        notificationLayout.setTextViewText(R.id.tv_name, nowPlaylist.getPlayingMuzic().name)

        notificationLayout.setOnClickPendingIntent(R.id.btn_play_or_pause, playIntent)
        notificationLayout.setOnClickPendingIntent(R.id.btn_stop, stopIntent)
        notificationLayout.setOnClickPendingIntent(R.id.btn_next, nextIntent)
        notificationLayout.setOnClickPendingIntent(R.id.btn_previous, previousIntent)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_play_arrow)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .build()
    }

    private fun createNotificationChannel() {

        Log.d("Muzic Service", "Create Chanel")

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
        this.listener.add(object : OnMuzicStateChangedListener {
            override fun onChanged(state: MuzicState) {
                listener(state)
            }
        })
    }

    fun handleListener(state: MuzicState) {
        for (it in listener) {
            it.onChanged(state)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
    }

}