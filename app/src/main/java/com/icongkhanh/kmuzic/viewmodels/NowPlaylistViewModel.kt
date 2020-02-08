package com.icongkhanh.kmuzic.viewmodels

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.icongkhanh.kmuzic.domain.models.Muzic
import com.icongkhanh.kmuzic.playermuzicservice.MuzicPlayer
import com.icongkhanh.kmuzic.playermuzicservice.MuzicState
import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicPlayingChangedListener
import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicStateChangedListener
import java.sql.Time
import java.util.*

class NowPlaylistViewModel(val muzicPlayer: MuzicPlayer) : ViewModel(), OnMuzicPlayingChangedListener, OnMuzicStateChangedListener {

    val listMusic = MutableLiveData<List<Muzic>>()
    val currentPlayingPos = MutableLiveData(-1)
    val stateMuzic = MutableLiveData<MuzicState>()
    val progressMusic = MutableLiveData<Float>(0f)
    var getProgressTask: TimerTask

    init {
        muzicPlayer.let {
            it.addOnStateChangedListener(this)
            it.addOnMuzicPlayingChangedListener(this)
        }


        getProgressTask = object : TimerTask() {
            override fun run() {
                val progress = muzicPlayer.getProgress()
                progress?.let {
                    progressMusic.postValue(it)
                }
            }

        }

        Timer().schedule(getProgressTask, 0,500)
    }

    fun onStart() {
        stateMuzic.postValue(muzicPlayer.muzicState)
        listMusic.postValue(muzicPlayer.getListMusic()?.map {
           it.toDomainModel()
        })
    }

    override fun onChanged(state: MuzicState) {
        stateMuzic.postValue(state)
    }

    override fun onChanged(muzic: com.icongkhanh.kmuzic.playermuzicservice.Muzic) {
        currentPlayingPos.postValue(listMusic.value?.indexOf(muzic.toDomainModel()))
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
        super.onCleared()
        getProgressTask.cancel()
    }
}