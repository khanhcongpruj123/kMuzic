package com.icongkhanh.kmuzic.fragments.home.homeviewpager.allmusic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.icongkhanh.kmuzic.domain.Result
import com.icongkhanh.kmuzic.domain.isSuccess
import com.icongkhanh.kmuzic.domain.usecases.LoadAllMusicUseCase
import com.icongkhanh.kmuzic.uimodels.ListMusicUiModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AllMusicFragmentViewModel(
    private val loadAllMusic: LoadAllMusicUseCase,
    private val isGrantedReadPermission: Boolean
) : ViewModel() {

    private val initialState = ListMusicUiModel()

    private val _listMusic = MutableLiveData<ListMusicUiModel>().apply {
        value = initialState
    }

    val listMusic: LiveData<ListMusicUiModel> = _listMusic.distinctUntilChanged()

    init {

        if (isGrantedReadPermission) _loadAllMusic()
    }

    companion object {
        val TAG = "MusicViewModel"
    }


    fun _loadAllMusic() {
        viewModelScope.launch {
            loadAllMusic(false).onStart {

            }.onEach { result ->
                when (result) {
                    is Result.Success -> if (result.isSuccess()) {
                        _listMusic.value = ListMusicUiModel(list = result.data, isLoading = false)
                    }
                    is Result.Loading -> {
                        _listMusic.value = ListMusicUiModel(isLoading = true)
                    }
                }
            }.collect()
        }
    }
}
