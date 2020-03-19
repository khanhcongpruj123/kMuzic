package com.icongkhanh.kmuzic

import android.app.Application
import com.icongkhanh.kmuzic.data.di.localModule
import com.icongkhanh.kmuzic.data.local.memory.MemoryMusicLoader
import com.icongkhanh.kmuzic.data.repositories.MuzicRepositoryImpl
import com.icongkhanh.kmuzic.domain.repositories.MuzicRepository
import com.icongkhanh.kmuzic.domain.usecases.GetMusicByIdUsecase
import com.icongkhanh.kmuzic.domain.usecases.LoadAllMusicUseCase
import com.icongkhanh.kmuzic.domain.usecases.LoadFavoriteMusicUsecase
import com.icongkhanh.kmuzic.domain.usecases.ToggleFavoriteMusicUsecase
import com.icongkhanh.kmuzic.fragments.MusicViewModel
import com.icongkhanh.kmuzic.fragments.home.HomeFragmentViewModel
import com.icongkhanh.kmuzic.fragments.home.homeviewpager.allmusic.AllMusicFragmentViewModel
import com.icongkhanh.kmuzic.fragments.home.homeviewpager.favouritemusic.FavoriteMusicViewModel
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
                                checkReadPermission(this@App)
                            )
                        }
                    },
                    module {
                        single { MuzicPlayer(this@App) }
                    },
                    module {

//                        viewModel {
//                            NowPlaylistViewModel(
//                                get(),
//                                get(),
//                                get()
//                            )
//                        }
                    },
                    module {
                        viewModel {
                            HomeFragmentViewModel(
                                get()
                            )
                        }
                    },
                    module {
                        factory { LoadFavoriteMusicUsecase(get()) }
                        viewModel {
                            FavoriteMusicViewModel(
                                get(),
                                get(),
                                checkReadPermission(androidContext())
                            )
                        }
                    },
                    module {
                        factory { GetMusicByIdUsecase(get()) }
                        factory { ToggleFavoriteMusicUsecase(get()) }
                        viewModel {
                            MusicViewModel(get(), get(), get())
                        }
                    }
                )
            )
        }
    }
}
