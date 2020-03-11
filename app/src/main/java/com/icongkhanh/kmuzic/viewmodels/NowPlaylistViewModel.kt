package com.icongkhanh.kmuzic.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.icongkhanh.kmuzic.domain.models.Muzic
import com.icongkhanh.kmuzic.playermuzicservice.MuzicPlayer
import com.icongkhanh.kmuzic.playermuzicservice.MuzicState
import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicPlayingChangedListener
import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicStateChangedListener
import java.util.*

class NowPlaylistViewModel(val muzicPlayer: MuzicPlayer) : ViewModel(), OnMuzicPlayingChangedListener, OnMuzicStateChangedListener {

    private var getProgressMuzicTimer: Timer
    val listMusic = MutableLiveData<List<Muzic>>()
    val currentPlayingMuzic = MutableLiveData<Muzic>()
    val stateMuzic = MutableLiveData<MuzicState>()
    val progressMusic = MutableLiveData<Float>(0f)

    init {
        muzicPlayer.let {
            it.addOnStateChangedListener(this)
            it.addOnMuzicPlayingChangedListener(this)
        }
        getProgressMuzicTimer = Timer()
    }

    fun onStart() {
        muzicPlayer.muzicState.let {
            stateMuzic.postValue(it)
            onChanged(it)
        }
        muzicPlayer.getCurrentMuzic()?.let {
            onChanged(it)
        }
        listMusic.postValue(muzicPlayer.getListMusic()?.map {
           it.toDomainModel()
        })
    }

    override fun onChanged(state: MuzicState) {
        stateMuzic.postValue(state)
        when(state) {
            MuzicState.IDLE, MuzicState.PAUSE -> {
                stopGetProgressTask()
            }
            MuzicState.PLAY -> {
                startGetProgressTask()
            }
        }
    }

    override fun onChanged(muzic: com.icongkhanh.kmuzic.playermuzicservice.Muzic) {
        currentPlayingMuzic.postValue(muzic.toDomainModel())
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

    override fun onCleared() {

        onStop()
        super.onCleared()
    }

    private fun startGetProgressTask() {

        getProgressMuzicTimer = Timer()

        val getProgressTask = object : TimerTask() {
            override fun run() {
                val progress = muzicPlayer.getProgress()
                progress?.let {
                    progressMusic.postValue(it)
                }
            }

        }

        getProgressMuzicTimer.schedule(getProgressTask, 0,500)
    }

    private fun stopGetProgressTask() {

        getProgressMuzicTimer.cancel()
    }

    fun onPressItemMuzic(it: Muzic) {
        muzicPlayer.play(com.icongkhanh.kmuzic.playermuzicservice.Muzic(
            it.id,
            it.name,
            it.authorName,
            it.isFavorite,
            it.path
        ))
    }

    fun onStop() {
        getProgressMuzicTimer.let {
            it.cancel()
        }
        muzicPlayer.unsubscribe(this)
    }
}