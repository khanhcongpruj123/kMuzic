package com.icongkhanh.kmuzic.data.local.memory

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.icongkhanh.kmuzic.domain.models.Muzic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.util.*

class MemoryMusicLoader(val context: Context) {


    suspend fun execute(): Flow<Muzic> = flow {

        val contentProvider = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val selection = " ${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA
        )

        val cursor = contentProvider.query(uri, projection, selection, null, sortOrder)
        cursor?.let {cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getString(0)
                val artist = cursor.getString(1)
                val title = cursor.getString(2)
                val path = cursor.getString(3)

                emit(Muzic(id, title, artist, false, path))
            }
            cursor.close()
        }
    }

    suspend fun getAllMusic() = flow<List<Muzic>> {

        val rootDir = Environment.getExternalStorageDirectory()

        val listMuzicPath = mutableListOf<String>()
        val listMuzic = mutableListOf<Muzic>()

        scanMusic(rootDir, listMuzicPath)

        listMuzicPath.forEach {

            Log.d(TAG, "Loaded: $it")

            val mr = MediaMetadataRetriever()
            mr.setDataSource(it)

            val name = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val author = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)


            listMuzic.add(Muzic(
                UUID.randomUUID().toString(),
                name,
                author,
                false,
                it
            ))

            mr.release()

            emit(listMuzic)
        }

    }

    fun scanMusic(dir: File, list: MutableList<String>) {

        if (dir.isFile) {
            val name = dir.name
            if (name.endsWith(".mp3")) {
                Log.d(TAG, "Loading: ${dir.absolutePath}")
                list.add(dir.absolutePath)
            } else return
        } else if (dir.isDirectory) {
            dir.listFiles().forEach {
                scanMusic(it, list)
            }
        }
    }

    companion object {
        val TAG = this::class.java.simpleName
    }
}