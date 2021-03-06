package com.icongkhanh.kmuzic.data.local.memory

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.icongkhanh.kmuzic.domain.models.Music
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.util.*

class MemoryMusicLoader(val context: Context) {


    suspend fun execute(): Flow<List<Music>> = flow {

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
        val list = mutableListOf<Music>()
        cursor?.let { c ->
            while (c.moveToNext()) {
                val id = c.getString(0)
                val artist = c.getString(1)
                val title = c.getString(2)
                val path = c.getString(3)

                list.add(Music(id, title, artist, false, path))
                emit(list)
            }
        }
        cursor?.close()
    }

    suspend fun getAllMusic(): MutableList<Music> {

        Log.d(TAG, "Get All Music!")
        val listMuzicPath = mutableListOf<String>()
        val listMuzic = mutableListOf<Music>()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            Log.d(TAG, "Scan Music After Q")
            scanMusicAfterQ(listMuzicPath)
        } else {
            Log.d(TAG, "Scan Music Before Q")
            val rootDir = Environment.getExternalStorageDirectory()
            scanMusicBeforeQ(rootDir, listMuzicPath)
        }

        val mr = MediaMetadataRetriever()
        listMuzicPath.forEach {
            mr.setDataSource(it)

            val name = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val author = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)


            listMuzic.add(
                Music(
                    UUID.randomUUID().toString(),
                    name,
                    author,
                    false,
                    it
                )
            )
        }
        mr.release()
        return listMuzic
    }

    fun scanMusicAfterQ(list: MutableList<String>) {

        val cr = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val projection = arrayOf(
            MediaStore.Audio.Media.DATA
        )
        val cursor = cr.query(uri, projection, selection, null, null)
        cursor?.let {
            while (it.moveToNext()) {
                val path = it.getString(0)
                list.add(path)
            }
        }
        cursor?.close()
    }

    fun scanMusicBeforeQ(dir: File, list: MutableList<String>) {

        if (dir.isFile) {
            val name = dir.name
            if (name.endsWith(".mp3")) {
                try {
                    Log.d(TAG, "Loading: ${dir.absolutePath}")

                    if (isValidFile(dir)) list.add(dir.absolutePath)

                } catch (ex: RuntimeException) {
                    Log.d(TAG, ex.message)
                    ex.printStackTrace()
                    return
                }
            } else return
        } else if (dir.isDirectory) {
            dir.listFiles()?.forEach {
                scanMusicBeforeQ(it, list)
            }
        }
    }

    companion object {
        val TAG = "MemoryMusicLoader"
    }

    fun isValidFile(musicFile: File): Boolean {
        if (musicFile.length() <= 0) return false

        val mr = MediaMetadataRetriever()
        mr.setDataSource(musicFile.absolutePath)

        val duration = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toInt()
        val name = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)

        mr.release()

        if (duration <= 5000 && name.isBlank() || name == null || name.isBlank()) return false
        return true
    }
}
