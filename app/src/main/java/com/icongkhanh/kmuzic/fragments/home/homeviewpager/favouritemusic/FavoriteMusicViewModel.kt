package com.icongkhanh.kmuzic.fragments.home.homeviewpager.favouritemusic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icongkhanh.kmuzic.domain.Result
import com.icongkhanh.kmuzic.domain.isSuccess
import com.icongkhanh.kmuzic.domain.usecases.LoadFavoriteMusicUsecase
import com.icongkhanh.kmuzic.playermuzicservice.Muzic
import com.icongkhanh.kmuzic.playermuzicservice.MuzicPlayer
import com.icongkhanh.kmuzic.uimodels.ListMusicUiModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FavoriteMusicViewModel(
    private val player: MuzicPlayer,
    private val loadFavoriteMusic: LoadFavoriteMusicUsecase,
    private val isReadPermissionGranted: Boolean
) : ViewModel() {

    private val _listMusic = MutableLiveData<ListMusicUiModel>()
    val listMusic: LiveData<ListMusicUiModel> = _listMusic

    init {
//        if (isReadPermissionGranted) {
//            loadMusic()
//        }
    }

    fun loadMusic() {
        viewModelScope.launch {
            loadFavoriteMusic().onEach { result ->
                when (result) {
                    is Result.Loading -> _listMusic.value =
                        ListMusicUiModel(isLoading = true)
                    is Result.Success -> {
                        if (result.isSuccess()) _listMusic.value =
                            ListMusicUiModel(
                                list = result.data,
                                isLoading = false
                            )
                        else _listMusic.value =
                            ListMusicUiModel(
                                isLoading = false
                            )
                    }
                    else -> {
                        _listMusic.value =
                            ListMusicUiModel(
                                isLoading = false
                            )
                    }
                }
            }.collect()
        }
    }

    fun playMusic(muzic: Muzic) {
        player.play(muzic)
    }
}
