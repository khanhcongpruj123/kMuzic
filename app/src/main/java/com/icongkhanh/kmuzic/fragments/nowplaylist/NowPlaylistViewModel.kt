package com.icongkhanh.kmuzic.fragments.nowplaylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.domain.usecases.AddMusicToFavorite
import com.icongkhanh.kmuzic.domain.usecases.GetMusicByIdUsecase
import com.icongkhanh.kmuzic.playermuzicservice.Muzic
import com.icongkhanh.kmuzic.playermuzicservice.MuzicPlayer
import com.icongkhanh.kmuzic.playermuzicservice.MuzicState
import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicPlayingChangedListener
import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicStateChangedListener
import com.icongkhanh.kmuzic.playermuzicservice.OnProgressChangedListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class NowPlaylistViewModel(
    val muzicPlayer: MuzicPlayer,
    val getMusicById: GetMusicByIdUsecase,
    val addMusicToFavorite: AddMusicToFavorite
) :
    ViewModel(),
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
        notifyPlayingMusic()
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

    fun notifyPlayingMusic() {
        currentPlayingMusic.value?.id?.let {
            viewModelScope.launch(Dispatchers.IO) {
                getMusicById(it).onEach {
                    _currentPlayingMuzic.value = it
                }.launchIn(viewModelScope)
            }
        }
    }

    fun addMusicToFavorite() {
        currentPlayingMusic.value?.id?.let {
            viewModelScope.launch(Dispatchers.IO) {
                addMusicToFavorite.invoke(it)
                notifyPlayingMusic()
            }
        }
    }
}
