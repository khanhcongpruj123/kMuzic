package com.icongkhanh.kmuzic

import android.app.Application
import com.icongkhanh.kmuzic.data.local.memory.MemoryMusicLoader
import com.icongkhanh.kmuzic.data.repositories.MuzicRepositoryImpl
import com.icongkhanh.kmuzic.domain.repositories.MuzicRepository
import com.icongkhanh.kmuzic.domain.usecases.LoadAllMusicUseCase
import com.icongkhanh.kmuzic.fragments.homeviewpager.allmusic.AllMusicFragmentViewModel
import com.icongkhanh.kmuzic.playermuzicservice.MuzicPlayer
import com.icongkhanh.kmuzic.viewmodels.NowPlaylistViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)

            modules(
                listOf(
                    module {
                        factory { LoadAllMusicUseCase(get()) }
                        factory { MuzicRepositoryImpl(get()) as MuzicRepository }
                        factory { MemoryMusicLoader(this@App) }
                        viewModel {
                            AllMusicFragmentViewModel(
                                get()
                            )
                        }
                    },
                    module {
                        single { MuzicPlayer(this@App) }
                    },
                    module {
                        viewModel { NowPlaylistViewModel(get()) }
                    }
                )
            )
        }
    }
}