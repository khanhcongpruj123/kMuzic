package com.icongkhanh.kmuzic.fragments.home.homeviewpager.allmusic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.icongkhanh.kmuzic.domain.usecases.LoadAllMusicUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AllMusicFragmentViewModel(
    private val loadAllMusic: LoadAllMusicUseCase,
    private val isGrantedReadPermission: Boolean
) : ViewModel() {

    private val initialState = AllMusicContract.ViewState.initial()

    private val _viewState = MutableLiveData<AllMusicContract.ViewState>().apply {
        value = initialState
    }

    val viewState: LiveData<AllMusicContract.ViewState> = _viewState.distinctUntilChanged()

    init {

        if (isGrantedReadPermission) _loadAllMusic()
    }

    companion object {
        val TAG = "MusicViewModel"
    }


    fun _loadAllMusic() {
        viewModelScope.launch {
            loadAllMusic(false).onStart {
                _viewState.value = AllMusicContract.ViewState(
                    music = emptyList(),
                    isLoading = true,
                    error = null
                )
            }.onEach {
                _viewState.value = AllMusicContract.ViewState(
                    music = it,
                    isLoading = false,
                    error = null
                )
            }.onCompletion {
                _viewState.value = _viewState.value!!.copy(isLoading = false)
            }.collect()
        }
    }
}
