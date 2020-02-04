package com.icongkhanh.kmuzic.fragments.homeviewpager

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icongkhanh.kmuzic.domain.models.Muzic
import com.icongkhanh.kmuzic.domain.usecases.LoadAllMusicUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MusicViewModel(
    val loadAllMusic: LoadAllMusicUseCase
) : ViewModel() {

    var listMuzic = MutableLiveData<List<Muzic>>()
    var _listMuzic = mutableListOf<Muzic>()

    fun onStart() {

        viewModelScope.launch(Dispatchers.IO) {
            _listMuzic.clear()
            loadAllMusic(true).collect {
                _listMuzic.add(it)
                listMuzic.postValue(_listMuzic)
            }
        }
    }


}