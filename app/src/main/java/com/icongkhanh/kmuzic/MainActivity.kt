package com.icongkhanh.kmuzic

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.icongkhanh.kmuzic.playermuzicservice.MuzicPlayer
import com.icongkhanh.kmuzic.playermuzicservice.MuzicService
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    val muzicPlayer: MuzicPlayer by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

        muzicPlayer.bind()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()

//        muzicPlayer.unbind()
    }

}

