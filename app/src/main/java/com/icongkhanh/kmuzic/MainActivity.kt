package com.icongkhanh.kmuzic

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.icongkhanh.kmuzic.fragments.MusicViewModel
import com.icongkhanh.kmuzic.playermuzicservice.MuzicPlayer
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    val muzicPlayer: MuzicPlayer by inject()
    val vm by viewModel<MusicViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        when {
            intent?.action == Intent.ACTION_VIEW -> {
                Log.d("AppLog", "${intent.data}")
            }
        }

    }

    override fun onResume() {
        super.onResume()

        muzicPlayer.bind()
    }

    override fun onStop() {
        super.onStop()
        muzicPlayer.unbind()
    }
}
