package com.icongkhanh.kmuzic.fragments.home.homeviewpager.allmusic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.domain.usecases.LoadAllMusicUseCase
import com.icongkhanh.kmuzic.playermuzicservice.Muzic
import com.icongkhanh.kmuzic.playermuzicservice.MuzicPlayer
import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicPlayingChangedListener
import com.icongkhanh.kmuzic.playermuzicservice.OnProgressChangedListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AllMusicFragmentViewModel(
    private val loadAllMusic: LoadAllMusicUseCase,
    private val player: MuzicPlayer,
    private val isGrantedReadPermission: Boolean
) : ViewModel(), OnMuzicPlayingChangedListener, OnProgressChangedListener {

    private val initialState = AllMusicContract.ViewState.initial()

    private val _viewState = MutableLiveData<AllMusicContract.ViewState>().apply {
        value = initialState
    }

    val viewState: LiveData<AllMusicContract.ViewState> = _viewState.distinctUntilChanged()

    private val _playingMusic = MutableLiveData<Music>()
    val playingMusic: LiveData<Music> = _playingMusic.distinctUntilChanged()

    private val _progressMusic = MutableLiveData<Float>()
    val progressMusic: LiveData<Float> = _progressMusic.distinctUntilChanged()

    init {

        player.addOnMuzicPlayingChangedListener(this)
        player.addOnProgressChangedListener(this)

        player.getCurrentMuzic()?.let {
            _playingMusic.value = it.toDomainModel()
        }

        player.getProgress().let {
            _progressMusic.value = it
        }

        if (isGrantedReadPermission) _loadAllMusic()
    }

    companion object {
        val TAG = "MusicViewModel"
    }

    override fun onChanged(muzic: Muzic) {
        _playingMusic.value = muzic.toDomainModel()
    }

    fun onStart() {
    }

    fun onStop() {
//        player.unsubscribe(this)
    }

    fun _loadAllMusic() {
        viewModelScope.launch {
            loadAllMusic(false).onStart {
//                Log.d(TAG, "On Start Load Music")
                _viewState.value = AllMusicContract.ViewState(
                    music = emptyList(),
                    isLoading = true,
                    error = null
                )
            }.onEach {
//                Log.d(TAG, "On Start Each")
                _viewState.value = AllMusicContract.ViewState(
                    music = it,
                    isLoading = false,
                    error = null
                )
            }.onCompletion {
                Log.d(TAG, "On Completed: ${_viewState.value?.music?.size}")
                _viewState.value = _viewState.value!!.copy(isLoading = false)
            }.collect()
        }
    }

    override fun onChanged(progress: Float) {
        viewModelScope.launch(Dispatchers.Main) {
            _progressMusic.value = progress
        }
    }

    fun play(muzic: Muzic) {
        player.play(muzic)
    }
}
