package com.icongkhanh.kmuzic.fragments.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.playermuzicservice.Muzic
import com.icongkhanh.kmuzic.playermuzicservice.MuzicPlayer
import com.icongkhanh.kmuzic.playermuzicservice.MuzicState
import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicPlayingChangedListener
import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicStateChangedListener
import com.icongkhanh.kmuzic.utils.mapToDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragmentViewModel(
    private val player: MuzicPlayer
) : ViewModel(), OnMuzicStateChangedListener, OnMuzicPlayingChangedListener {

    private val _musicState = MutableLiveData<MuzicState>()
    val muzicState: LiveData<MuzicState> = _musicState.distinctUntilChanged()

    private val _playingMusic = MutableLiveData<Music?>(null)
    val playingMusic: LiveData<Music?> = _playingMusic.distinctUntilChanged()

    init {

        _musicState.value = player.getMusicState()
        _playingMusic.value = player.getCurrentMuzic()?.mapToDomainModel()
        Log.d(TAG, "playing music: ${player.getCurrentMuzic()?.mapToDomainModel()?.name}")
    }

    override fun onChanged(state: MuzicState) {
        _musicState.value = state
    }

    override fun onChanged(muzic: Muzic) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) { _playingMusic.value = muzic.mapToDomainModel() }
        }
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
//        Log.d(TAG, "On Cleared!")
    }

    companion object {
        val TAG = "HomeFragmentViewModel"
    }
}
