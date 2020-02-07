package com.icongkhanh.kmuzic.playermuzicservice

import android.annotation.TargetApi
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
    }

    private val CHANNEL_ID: String = "kMuzic Service"
    val binder = LocalBinder()

    val player = MediaPlayer()
    val nowPlaylist = NowPlaylist()


    override fun onCreate() {
        super.onCreate()

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        createNotificationChannel()

        Log.d("Muzic Service", "On start command")

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

        Log.d("Muzic Service", "Play music")

        startForeground(1, buidNotification())

        if (player.isPlaying) player.stop()
        player.reset()
        player.let {
            it.setDataSource(nowPlaylist.getPlayingMuzic().path)
            it.prepare()
            it.start()
        }

    }

    fun isExistedMuzic(muzic: Muzic) : Boolean {
        return nowPlaylist.isExistedMuzic(muzic)
    }

    fun pause() {
        if (player.isPlaying) player.pause()
    }

    fun stop() {
        player.stop()
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
            nowPlaylist.currentPosition = index
        } else {
            index = nowPlaylist.addMusic(muzic)
            nowPlaylist.currentPosition = index
            play()
        }
    }

    fun playOrPause() {
        if (player.isPlaying) player.pause()
        else {
            if (nowPlaylist.currentPosition == -1) return
            else {
                player.start()
            }
        }
    }

    fun isPlaying(): Boolean {
        return player?.isPlaying
    }

    fun buidNotification(): Notification {

        Log.d("AppLog", "Build Notification")

        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }

        val notificationLayout = RemoteViews(packageName, R.layout.mini_controller_notifi)
        val notificationLayoutExpanded = RemoteViews(packageName, R.layout.mini_controller_notifi)



        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_play_arrow)
//            .setContentTitle("kMuzic")
            .setContentIntent(pendingIntent)
//            .setContentText("Playing...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContent(notificationLayout)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
//            .setCustomBigContentView(notificationLayoutExpanded)
            .build()


//        val notificationLayout = RemoteViews(packageName, R.layout.mini_controller_notifi)
////        val notificationLayoutExpanded = RemoteViews(packageName, R.layout.notification_large)
//
//        val pendingIntent = Intent(this, MainActivity::class.java).let {
//            PendingIntent.getActivity(this, 0, it, 0)
//        }
//
//        return NotificationCompat.Builder(this, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_play_arrow)
//            .setContentIntent(pendingIntent)
//            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
//            .setCustomContentView(notificationLayout)
//            .build()
    }
    private fun createNotificationChannel() {

        Log.d("Muzic Service", "Create Chanel")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "kMuzic"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        player.stop()
    }

}