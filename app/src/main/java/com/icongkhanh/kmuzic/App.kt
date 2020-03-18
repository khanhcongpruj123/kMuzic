package com.icongkhanh.kmuzic

import android.app.Application
import com.icongkhanh.kmuzic.data.di.localModule
import com.icongkhanh.kmuzic.data.local.memory.MemoryMusicLoader
import com.icongkhanh.kmuzic.data.repositories.MuzicRepositoryImpl
import com.icongkhanh.kmuzic.domain.repositories.MuzicRepository
import com.icongkhanh.kmuzic.domain.usecases.AddMusicToFavorite
import com.icongkhanh.kmuzic.domain.usecases.GetMusicByIdUsecase
import com.icongkhanh.kmuzic.domain.usecases.LoadAllMusicUseCase
import com.icongkhanh.kmuzic.fragments.home.HomeFragmentViewModel
import com.icongkhanh.kmuzic.fragments.home.homeviewpager.allmusic.AllMusicFragmentViewModel
import com.icongkhanh.kmuzic.fragments.nowplaylist.NowPlaylistViewModel
import com.icongkhanh.kmuzic.playermuzicservice.MuzicPlayer
import com.icongkhanh.kmuzic.utils.PermissionUtils.checkReadPermission
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
                    localModule,
                    module {
                        factory { LoadAllMusicUseCase(get()) }
                        factory { MuzicRepositoryImpl(get(), get(), get()) as MuzicRepository }
                        factory { MemoryMusicLoader(this@App) }
                        viewModel {

                            AllMusicFragmentViewModel(
                                get(),
                                get(),
                                checkReadPermission(this@App)
                            )
                        }
                    },
                    module {
                        single { MuzicPlayer(this@App) }
                    },
                    module {
                        factory { GetMusicByIdUsecase(get()) }
                        factory { AddMusicToFavorite(get()) }
                        viewModel {
                            NowPlaylistViewModel(
                                get(),
                                get(),
                                get()
                            )
                        }
                    },
                    module {
                        viewModel {
                            HomeFragmentViewModel(
                                get()
                            )
                        }
                    }
                )
            )
        }
    }
}
