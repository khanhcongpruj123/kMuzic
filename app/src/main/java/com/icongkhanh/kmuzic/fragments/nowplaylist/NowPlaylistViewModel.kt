package com.icongkhanh.kmuzic.fragments.nowplaylist

import androidx.lifecycle.*
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.playermuzicservice.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NowPlaylistViewModel(val muzicPlayer: MuzicPlayer) : ViewModel(),
    OnMuzicPlayingChangedListener, OnMuzicStateChangedListener, OnProgressChangedListener {


    private val _listMusic = MutableLiveData<List<Music>>()
    val listMusic: LiveData<List<Music>> = _listMusic.distinctUntilChanged()

    private val _currentPlayingMuzic = MutableLiveData<Music>()
    val currentPlayingMusic: LiveData<Music> = _currentPlayingMuzic.distinctUntilChanged()

    private val _stateMuzic = MutableLiveData<MuzicState>()
    val stateMuzic: LiveData<MuzicState> = _stateMuzic.distinctUntilChanged()

    private val _progressMusic = MutableLiveData<Float>(0f)
    val progressMusic: LiveData<Float> = _progressMusic.distinctUntilChanged()


    init {
        muzicPlayer.let {
            it.addOnStateChangedListener(this)
            it.addOnMuzicPlayingChangedListener(this)
            it.addOnProgressChangedListener(this)
        }

        muzicPlayer.getMusicState().let {
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
        _stateMuzic.value = state
    }

    override fun onChanged(muzic: Muzic) {
        _currentPlayingMuzic.value = muzic.toDomainModel()
        _listMusic.value = muzicPlayer.getListMusic()?.map { it.toDomainModel() }
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

    fun play(muzic: Muzic) {
        muzicPlayer.play(muzic)
    }

    override fun onChanged(progress: Float) {
        viewModelScope.launch(Dispatchers.Main) {
            _progressMusic.value = progress
        }
    }

    companion object {
        val TAG = "NowPlaylistViewModel"
    }
}