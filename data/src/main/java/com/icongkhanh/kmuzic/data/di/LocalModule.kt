package com.icongkhanh.kmuzic.data.di

import com.icongkhanh.kmuzic.data.local.database.kMuzicDatabase
import com.icongkhanh.kmuzic.data.prefs.Prefs
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val localModule = module {
    single { kMuzicDatabase.buildDatabase(androidContext()) }
    factory { (get() as kMuzicDatabase).kMuzicDao() }
    factory { Prefs(androidContext()) }
}
