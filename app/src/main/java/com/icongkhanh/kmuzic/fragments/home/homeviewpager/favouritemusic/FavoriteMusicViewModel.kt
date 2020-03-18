package com.icongkhanh.kmuzic.fragments.home.homeviewpager.favouritemusic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.domain.usecases.LoadFavoriteMusicUsecase
import com.icongkhanh.kmuzic.playermuzicservice.Muzic
import com.icongkhanh.kmuzic.playermuzicservice.MuzicPlayer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FavoriteMusicViewModel(
    private val player: MuzicPlayer,
    private val loadFavoriteMusic: LoadFavoriteMusicUsecase,
    private val isReadPermissionGranted: Boolean
) : ViewModel() {

    private val _listMusic = MutableLiveData<List<Music>>()
    val listMusic: LiveData<List<Music>> = _listMusic

    init {
        if (isReadPermissionGranted) {
            loadMusic()
        }
    }

    fun loadMusic() {
        viewModelScope.launch {
            loadFavoriteMusic().onEach {
                _listMusic.value = it
            }.collect()
        }
    }

    fun playMusic(muzic: Muzic) {
        player.play(muzic)
    }
}
