package com.icongkhanh.kmuzic.fragments.homeviewpager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.icongkhanh.kmuzic.domain.models.Muzic
import com.icongkhanh.kmuzic.domain.usecases.LoadAllMusicUseCase
import kotlinx.coroutines.Dispatchers

class AllMusicFragmentViewModel(
    val loadAllMusic: LoadAllMusicUseCase
) : ViewModel() {

    var listMuzic = liveData<List<Muzic>>(Dispatchers.IO) {
        val result = loadAllMusic(true).asLiveData()
        emitSource(result)
    }

    companion object {
        val TAG = "MusicViewModel"
    }

}