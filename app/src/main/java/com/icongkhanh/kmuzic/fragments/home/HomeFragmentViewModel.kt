package com.icongkhanh.kmuzic.fragments.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.playermuzicservice.Muzic
import com.icongkhanh.kmuzic.playermuzicservice.MuzicPlayer
import com.icongkhanh.kmuzic.playermuzicservice.MuzicState
import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicPlayingChangedListener
import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicStateChangedListener
import com.icongkhanh.kmuzic.utils.mapToDomainModel

class HomeFragmentViewModel(
    private val player: MuzicPlayer
) : ViewModel(), OnMuzicStateChangedListener, OnMuzicPlayingChangedListener {

    private val _musicState = MutableLiveData<MuzicState>()
    val muzicState: LiveData<MuzicState> = _musicState.distinctUntilChanged()

    private val _playingMusic = MutableLiveData<Music>()
    val playingMusic: LiveData<Music> = _playingMusic.distinctUntilChanged()

    init {

        _musicState.value = player.getMusicState()
        player.getCurrentMuzic()?.let {
            _playingMusic.value = it.mapToDomainModel()
        }
    }

    override fun onChanged(state: MuzicState) {
        _musicState.value = state
    }

    override fun onChanged(muzic: Muzic) {
        _playingMusic.value = muzic.mapToDomainModel()
    }

    fun playOrPause() {
        player.playOrPause()
    }

    fun onStop() {
        player.unsubscribe(this)
    }

    fun onStart() {
        player.addOnMuzicPlayingChangedListener(this)
        player.addOnStateChangedListener(this)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "On Cleared!")
    }

    companion object {
        val TAG = "HomeFragmentViewModel"
    }
}
