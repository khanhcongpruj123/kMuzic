package com.icongkhanh.kmuzic.fragments.homeviewpager.allmusic

import android.util.Log
import androidx.lifecycle.*
import com.icongkhanh.kmuzic.domain.usecases.LoadAllMusicUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AllMusicFragmentViewModel(
    val loadAllMusic: LoadAllMusicUseCase
) : ViewModel() {

    private val initialState = AllMusicContract.ViewState.initial()

    private val _viewState = MutableLiveData<AllMusicContract.ViewState>().apply {
        value = initialState
    }

    val viewState: LiveData<AllMusicContract.ViewState> = _viewState.distinctUntilChanged()

    init {
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

    companion object {
        val TAG = "MusicViewModel"
    }

}