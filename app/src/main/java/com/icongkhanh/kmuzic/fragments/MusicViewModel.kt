package com.icongkhanh.kmuzic.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.icongkhanh.kmuzic.domain.Result
import com.icongkhanh.kmuzic.domain.isSuccess
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.domain.usecases.GetMusicByIdUsecase
import com.icongkhanh.kmuzic.domain.usecases.GetNowPlaylistUsecase
import com.icongkhanh.kmuzic.domain.usecases.SaveNowPlaylistUsecase
import com.icongkhanh.kmuzic.domain.usecases.ToggleFavoriteMusicUsecase
import com.icongkhanh.kmuzic.playermuzicservice.Muzic
import com.icongkhanh.kmuzic.playermuzicservice.MuzicPlayer
import com.icongkhanh.kmuzic.playermuzicservice.MuzicState
import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicPlayingChangedListener
import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicStateChangedListener
import com.icongkhanh.kmuzic.playermuzicservice.OnNowPlayListChangedListener
import com.icongkhanh.kmuzic.playermuzicservice.OnProgressChangedListener
import com.icongkhanh.kmuzic.uimodels.ListMusicUiModel
import com.icongkhanh.kmuzic.utils.mapToDomainModel
import com.icongkhanh.kmuzic.utils.mapToServiceModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicViewModel(
    private val player: MuzicPlayer,
    val getMusicById: GetMusicByIdUsecase,
    val saveNowPlaylist: SaveNowPlaylistUsecase,
    val getNowPlaylist: GetNowPlaylistUsecase,
    val toggleFavoriteMusic: ToggleFavoriteMusicUsecase
) :
    OnMuzicStateChangedListener,
    OnMuzicPlayingChangedListener,
    OnProgressChangedListener,
    OnNowPlayListChangedListener,
    ViewModel() {

    private val _stateMusic = MutableLiveData<MuzicState>()
    val stateMusic: LiveData<MuzicState> = _stateMusic.distinctUntilChanged()

    private val _progressMusic = MutableLiveData<Float>()
    val progressMusic: LiveData<Float> = _progressMusic.distinctUntilChanged()

    private val _playingMusic = MutableLiveData<Music>()
    val playingMusic: LiveData<Music> = _playingMusic.distinctUntilChanged()

    private val _nowplaylist = MutableLiveData<ListMusicUiModel>()
    val nowplaylist: LiveData<ListMusicUiModel> = _nowplaylist.distinctUntilChanged()

    override fun onChanged(state: MuzicState) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) { _stateMusic.value = state }
        }
    }

    override fun onChanged(muzic: Muzic) {
        notifyPlayingMusic(muzic.mapToDomainModel())
    }

    override fun onChanged(progress: Float) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) { _progressMusic.value = progress }
        }
    }

    override fun onChanged(list: List<Muzic>) {
        viewModelScope.launch {
            saveNowPlaylist(list.map { it.mapToDomainModel() })
            withContext(Dispatchers.Main) {
                _nowplaylist.value = ListMusicUiModel(
                    list = list.map { it.mapToDomainModel() }
                )
            }
        }
    }

    init {
        _progressMusic.value = player.getProgress()
        _playingMusic.value = player.getCurrentMuzic()?.mapToDomainModel()
        _stateMusic.value = player.getMusicState()
        _nowplaylist.value = ListMusicUiModel(
            list = player.getListMusic()?.map { it.mapToDomainModel() } ?: emptyList()
        )

        onStart()
    }

    fun play(it: Music) {
        viewModelScope.launch(Dispatchers.Default) {
            player.play(it.mapToServiceModel())
        }
    }

    fun onStart() {
        setListener()
    }

    fun onStop() {
        player.unsubscribe(this)
    }

    fun setListener() {
        player.addOnMuzicPlayingChangedListener(this)
        player.addOnStateChangedListener(this)
        player.addOnProgressChangedListener(this)
        player.addOnNowplaylistChangedListener(this)
    }

    fun notifyPlayingMusic(music: Music) {
        viewModelScope.launch {
            getMusicById(music.id).onEach {
                when (it) {
                    is Result.Success -> withContext(Dispatchers.Main) {
                        if (it.isSuccess()) _playingMusic.value = it.data
                    }
                }
            }.collect()
        }

    }

    fun toggleFavoriteMusic() {
        playingMusic.value?.let {
            viewModelScope.launch(Dispatchers.IO) {
                toggleFavoriteMusic.invoke(it.id)
                notifyPlayingMusic(it)
            }
        }
    }

    fun onPressPlayOrPause() {
        viewModelScope.launch(Dispatchers.Default) {
            player.playOrPause()
        }
    }

    fun onPressNext() {
        viewModelScope.launch(Dispatchers.Default) {
            player.next()
        }
    }

    fun onPressPrevious() {
        viewModelScope.launch(Dispatchers.Default) {
            player.previous()
        }
    }

    companion object {
        val TAG = "MusicViewModel"
    }

}
