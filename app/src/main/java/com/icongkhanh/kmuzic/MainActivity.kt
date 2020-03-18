package com.icongkhanh.kmuzic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.icongkhanh.kmuzic.playermuzicservice.MuzicPlayer
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    val muzicPlayer: MuzicPlayer by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
