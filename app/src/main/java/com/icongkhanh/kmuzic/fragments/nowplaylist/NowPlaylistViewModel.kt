package com.icongkhanh.kmuzic.fragments.nowplaylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.playermuzicservice.MuzicPlayer
import com.icongkhanh.kmuzic.playermuzicservice.MuzicState
import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicPlayingChangedListener
import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicStateChangedListener
import java.util.*

class NowPlaylistViewModel(val muzicPlayer: MuzicPlayer) : ViewModel(), OnMuzicPlayingChangedListener, OnMuzicStateChangedListener {

    private var getProgressMuzicTimer = Timer()

    private val _listMusic = MutableLiveData<List<Music>>()
    val listMusic: LiveData<List<Music>> = _listMusic

    private val _currentPlayingMuzic = MutableLiveData<Music>()
    val currentPlayingMusic: LiveData<Music> = _currentPlayingMuzic

    private val _stateMuzic = MutableLiveData<MuzicState>()
    val stateMuzic: LiveData<MuzicState> = _stateMuzic

    private val _progressMusic = MutableLiveData<Float>(0f)
    val progressMusic: LiveData<Float> = _progressMusic


    init {
        muzicPlayer.let {
            it.addOnStateChangedListener(this)
            it.addOnMuzicPlayingChangedListener(this)
        }

        muzicPlayer.muzicState.let {
            _stateMuzic.postValue(it)
            onChanged(it)
        }
        muzicPlayer.getCurrentMuzic()?.let {
            onChanged(it)
        }
        _listMusic.postValue(muzicPlayer.getListMusic()?.map {
            it.toDomainModel()
        })
    }

    override fun onChanged(state: MuzicState) {
        _stateMuzic.postValue(state)
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
        _currentPlayingMuzic.postValue(muzic.toDomainModel())
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
                    _progressMusic.postValue(it)
                }
            }

        }

        getProgressMuzicTimer.schedule(getProgressTask, 0,500)
    }

    private fun stopGetProgressTask() {
        getProgressMuzicTimer.cancel()
    }

    fun onStop() {
        getProgressMuzicTimer.cancel()
        muzicPlayer.unsubscribe(this)
    }
}