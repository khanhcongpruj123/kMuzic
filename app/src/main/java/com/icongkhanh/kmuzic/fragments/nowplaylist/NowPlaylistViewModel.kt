package com.icongkhanh.kmuzic.fragments.nowplaylist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.playermuzicservice.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NowPlaylistViewModel(val muzicPlayer: MuzicPlayer) : ViewModel(),
    OnMuzicPlayingChangedListener, OnMuzicStateChangedListener, OnProgressChangedListener {


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
            it.addOnProgressChangedListener(this)
        }

        muzicPlayer.muzicState.let {
            _stateMuzic.value = it
        }

        muzicPlayer.getCurrentMuzic()?.let {
            _currentPlayingMuzic.value = it.toDomainModel()
        }
        _listMusic.value = muzicPlayer.getListMusic()?.map {
            it.toDomainModel()
        }

        _progressMusic.value = muzicPlayer.getProgress()
    }

    override fun onChanged(state: MuzicState) {
        _stateMuzic.postValue(state)

        /**
         * stop observe progress when idle and pause
         * */
//        when(state) {
//            MuzicState.IDLE, MuzicState.PAUSE -> {
//                stopGetProgressTask()
//            }
//            MuzicState.PLAY -> {
//                startGetProgressTask()
//            }
//        }
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

    fun onStop() {
        muzicPlayer.unsubscribe(this)
    }

    override fun onChanged(progress: Float) {
        Log.d(TAG, "On Progress Changed")
        viewModelScope.launch(Dispatchers.Main) {
            _progressMusic.value = progress
        }
    }

    companion object {
        val TAG = "NowPlaylistViewModel"
    }
}