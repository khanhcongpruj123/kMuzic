package com.icongkhanh.kmuzic.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.icongkhanh.kmuzic.data.local.database.dao.kMuzicDao
import com.icongkhanh.kmuzic.data.local.database.model.MusicDB
import com.icongkhanh.kmuzic.data.local.database.model.PlaylistAndMusicRef
import com.icongkhanh.kmuzic.data.local.database.model.PlaylistDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [MusicDB::class, PlaylistDB::class, PlaylistAndMusicRef::class],
    version = 2,
    exportSchema = false
)
abstract class kMuzicDatabase : RoomDatabase() {

    abstract fun kMuzicDao(): kMuzicDao

    companion object {
        private const val DATABASE_NAME = "kMuzic-db"

        @Volatile
        private var instance: kMuzicDatabase? = null

        fun getInstance(context: Context): kMuzicDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        fun buildDatabase(context: Context): kMuzicDatabase {
            val db = Room.databaseBuilder(
                context,
                kMuzicDatabase::class.java,
                DATABASE_NAME
            ).build()

            CoroutineScope(Dispatchers.IO).launch {
                db.kMuzicDao().insertPlaylist(
                    PlaylistDB(
                        "8664e1aa-6fc1-4801-b9fd-c4858b92da09",
                        "NowPlaylist"
                    )
                )
            }

            return db
        }
    }
}
