package com.icongkhanh.kmuzic.fragments.homeviewpager

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icongkhanh.kmuzic.domain.models.Muzic
import com.icongkhanh.kmuzic.domain.usecases.LoadAllMusicUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AllMusicFragmentViewModel(
    val loadAllMusic: LoadAllMusicUseCase
) : ViewModel() {

    var listMuzic = MutableLiveData<List<Muzic>>()
    var _listMuzic = mutableListOf<Muzic>()

    companion object {
        val TAG = "MusicViewModel"
    }

    fun onStart() {
        loadMusic()
    }

    fun loadMusic() {
        viewModelScope.launch(Dispatchers.IO) {
            _listMuzic.clear()
            loadAllMusic(true).collect {
                Log.d(TAG, "Music: ${it}")
                _listMuzic.clear()
                _listMuzic.addAll(it)
                listMuzic.postValue(_listMuzic)
            }
        }
    }

}