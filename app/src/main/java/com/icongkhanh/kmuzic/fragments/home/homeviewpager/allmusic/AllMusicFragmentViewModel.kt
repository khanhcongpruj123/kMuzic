package com.icongkhanh.kmuzic.fragments.home.homeviewpager.allmusic

import android.util.Log
import androidx.lifecycle.*
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.domain.usecases.LoadAllMusicUseCase
import com.icongkhanh.kmuzic.playermuzicservice.Muzic
import com.icongkhanh.kmuzic.playermuzicservice.MuzicPlayer
import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicPlayingChangedListener
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AllMusicFragmentViewModel(
    private val loadAllMusic: LoadAllMusicUseCase,
    private val player: MuzicPlayer,
    private val isGrantedReadPermission: Boolean
) : ViewModel(), OnMuzicPlayingChangedListener {

    private val initialState = AllMusicContract.ViewState.initial()

    private val _viewState = MutableLiveData<AllMusicContract.ViewState>().apply {
        value = initialState
    }

    val viewState: LiveData<AllMusicContract.ViewState> = _viewState.distinctUntilChanged()

    private val _playingMusic = MutableLiveData<Music>()
    val playingMusic: LiveData<Music> = _playingMusic.distinctUntilChanged()

    init {

        player.addOnMuzicPlayingChangedListener(this)

        player.getCurrentMuzic()?.let {
            _playingMusic.value = it.toDomainModel()
        }

        _loadAllMusic()
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
            loadAllMusic(true).onStart {
                Log.d(TAG, "On Start Load Music")
                _viewState.value = AllMusicContract.ViewState(
                    music = emptyList(),
                    isLoading = true,
                    error = null
                )
            }.onEach {
                Log.d(TAG, "On Start Each")
                _viewState.value = AllMusicContract.ViewState(
                    music = it,
                    isLoading = true,
                    error = null
                )
            }.onCompletion {
                Log.d(TAG, "On Completed: ${_viewState.value?.music?.size}")
                _viewState.value = _viewState.value!!.copy(isLoading = false)
            }.launchIn(viewModelScope)
        }
    }

}