//package com.icongkhanh.kmuzic.fragments.nowplaylist
//
//import android.util.Log
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.distinctUntilChanged
//import androidx.lifecycle.viewModelScope
//import com.icongkhanh.kmuzic.domain.models.Music
//import com.icongkhanh.kmuzic.domain.usecases.ToggleFavoriteMusicUsecase
//import com.icongkhanh.kmuzic.domain.usecases.GetMusicByIdUsecase
//import com.icongkhanh.kmuzic.playermuzicservice.Muzic
//import com.icongkhanh.kmuzic.playermuzicservice.MuzicPlayer
//import com.icongkhanh.kmuzic.playermuzicservice.MuzicState
//import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicPlayingChangedListener
//import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicStateChangedListener
//import com.icongkhanh.kmuzic.playermuzicservice.OnProgressChangedListener
//import com.icongkhanh.kmuzic.utils.mapToDomainModel
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.collect
//import kotlinx.coroutines.flow.launchIn
//import kotlinx.coroutines.flow.onEach
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class NowPlaylistViewModel(
//    val getMusicById: GetMusicByIdUsecase,
//    val toggleFavoriteMusicUsecase: ToggleFavoriteMusicUsecase
//) :
//    ViewModel() {
//
//
//    override fun onCleared() {
//        super.onCleared()
//    }
//
//    companion object {
//        val TAG = "NowPlaylistViewModel"
//    }
//}
