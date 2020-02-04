package com.icongkhanh.kmuzic.data.local.memory

import android.content.Context
import android.provider.MediaStore
import com.icongkhanh.kmuzic.domain.models.Muzic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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

    companion object {
        val TAG = "AppLog"
    }
}