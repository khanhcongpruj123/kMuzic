package com.icongkhanh.kmuzic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.icongkhanh.kmuzic.data.local.memory.MemoryMusicLoader
import com.icongkhanh.kmuzic.domain.models.Muzic
class MainActivity : AppCompatActivity() {

    val res = mutableListOf<Muzic>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val musicLoader = MemoryMusicLoader(this)
    }
}
