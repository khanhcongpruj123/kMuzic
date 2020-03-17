package com.icongkhanh.kmuzic.fragments.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.playermuzicservice.*

class HomeFragmentViewModel(
    private val player: MuzicPlayer
) : ViewModel(), OnMuzicStateChangedListener, OnMuzicPlayingChangedListener {

    private val _musicState = MutableLiveData<MuzicState>()
    val muzicState: LiveData<MuzicState> = _musicState.distinctUntilChanged()

    private val _playingMusic = MutableLiveData<Music>()
    val playingMusic: LiveData<Music> = _playingMusic.distinctUntilChanged()

    init {

        player.addOnMuzicPlayingChangedListener(this)
        player.addOnStateChangedListener(this)

        _musicState.value = player.getMusicState()
        player.getCurrentMuzic()?.let {
            _playingMusic.value = it.toDomainModel()
        }
    }

    override fun onChanged(state: MuzicState) {
        _musicState.value = state
    }

    override fun onChanged(muzic: Muzic) {
        _playingMusic.value = muzic.toDomainModel()
    }

    fun playOrPause() {
        player.playOrPause()
    }

    fun onStop() {
//        player.unsubscribe(this)
    }

    fun onStart() {

    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "On Cleared!")
    }

    companion object {
        val TAG = "HomeFragmentViewModel"
    }

}