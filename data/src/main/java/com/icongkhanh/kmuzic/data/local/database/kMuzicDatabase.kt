package com.icongkhanh.kmuzic.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.icongkhanh.kmuzic.data.local.database.dao.kMuzicDao
import com.icongkhanh.kmuzic.data.local.database.model.MusicDB

@Database(entities = [MusicDB::class], version = 1, exportSchema = false)
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
            return Room.databaseBuilder(
                context,
                kMuzicDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}
