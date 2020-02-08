package com.icongkhanh.kmuzic.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.icongkhanh.kmuzic.domain.models.Muzic
import com.icongkhanh.kmuzic.playermuzicservice.MuzicPlayer
import com.icongkhanh.kmuzic.playermuzicservice.MuzicState
import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicPlayingChangedListener
import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicStateChangedListener

class NowPlaylistViewModel(val muzicPlayer: MuzicPlayer) : ViewModel(), OnMuzicPlayingChangedListener, OnMuzicStateChangedListener {

    val listMusic = MutableLiveData<List<Muzic>>()
    val currentPlayingPos = MutableLiveData(-1)
    val stateMuzic = MutableLiveData<MuzicState>()

    init {
        muzicPlayer.let {
            it.addOnStateChangedListener(this)
            it.addOnMuzicPlayingChangedListener(this)
        }

    }

    fun onStart() {
        stateMuzic.postValue(muzicPlayer.muzicState)
        listMusic.postValue(muzicPlayer.getListMusic()?.map {
            Muzic(
                it.id,
                it.name,
                it.author,
                it.isFavorite,
                it.path
            )
        })
    }

    override fun onChanged(state: MuzicState) {
        stateMuzic.postValue(state)
    }

    override fun onChanged(muzic: com.icongkhanh.kmuzic.playermuzicservice.Muzic) {
        currentPlayingPos.postValue(listMusic.value?.indexOf(muzic.toDomainModel()))
    }

    fun onPressPlayOrPause() {
        muzicPlayer.playOrPause()
    }

    fun onPressNext() {
        muzicPlayer.next()
    }

    fun onPressPrevious() {
        muzicPlayer.previous()
    }
}