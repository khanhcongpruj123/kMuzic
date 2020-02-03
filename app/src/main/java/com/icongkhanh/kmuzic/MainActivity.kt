package com.icongkhanh.kmuzic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.icongkhanh.kmuzic.data.local.memory.MemoryMusicLoader
import com.icongkhanh.kmuzic.domain.models.Muzic
import com.icongkhanh.kmuzic.playmuzicservice.MuzicService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    val res = mutableListOf<Muzic>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val musicLoader = MemoryMusicLoader(this)

        CoroutineScope(Dispatchers.IO).launch {
            musicLoader.execute().collect {
                res.add(it)
            }

            val intent = Intent(this@MainActivity, MuzicService::class.java)
            intent.action = MuzicService.PLAY
            intent.putExtra("muzic", com.icongkhanh.kmuzic.playmuzicservice.Muzic(
                res[0].id,
                res[0].name,
                res[0].authorName,
                res[0].isFavorite,
                res[0].path
            ))
            startService(intent)
        }
    }
}
