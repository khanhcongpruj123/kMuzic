package com.icongkhanh.kmuzic.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.icongkhanh.kmuzic.adapters.ListMusicAdapter
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.uimodels.ListMusicUiModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

abstract class BaseMusicFragment : Fragment() {

    lateinit var adapter: ListMusicAdapter
    lateinit var _layoutManager: RecyclerView.LayoutManager
    private lateinit var listMusic: RecyclerView

    val musicVM by sharedViewModel<MusicViewModel>()

    abstract fun getMusicRecyclerView(): RecyclerView

    abstract fun getLayoutManager(): RecyclerView.LayoutManager

    abstract fun getListMusic(): LiveData<ListMusicUiModel>

    override fun onStart() {
        super.onStart()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ListMusicAdapter(requireContext())
        _layoutManager = getLayoutManager()
        listMusic = getMusicRecyclerView()

        adapter.setOnPressItem {
            musicVM.play(it)
            onMusicItemClicked(it)
        }

        adapter.setOnLongPressItem {
            onMusicItemLongClicked(it)
        }

        listMusic.adapter = adapter
        listMusic.layoutManager = _layoutManager

        subscribeUi()
    }

    override fun onStop() {
        super.onStop()
//        musicVM.onStop()
    }

    private fun subscribeUi() {

        getListMusic().observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.updateListMusic(it.list)
                onListMusicChanged()
            }
        })

        musicVM.playingMusic.observe(viewLifecycleOwner, Observer {
            if (it != null) {
//                Log.d("AppLog", "playing music: ${it.name}")
                adapter.updatePlayingMusic(it)
                onPlayingMusicChanged()
            }
        })

        musicVM.progressMusic.observe(viewLifecycleOwner, Observer {
            if (it != null) {
//                Log.d("AppLog", "progress music: ${it}")
                adapter.updateProgress(it)
                onProgressChanged()
            }
        })

        musicVM.stateMusic.observe(viewLifecycleOwner, Observer {
            onStateMusicChanged()
        })
    }

    protected open fun onListMusicChanged() {
        //Nothing wait u override
    }

    protected open fun onMusicItemLongClicked(it: Music) {
        //Nothing wait u override
    }

    protected open fun onMusicItemClicked(it: Music) {

    }  //Nothing wait u override

    protected open fun onPlayingMusicChanged() {
        //Nothing wait u override
    }

    protected open fun onProgressChanged() {
        //Nothing wait u override
    }

    protected open fun onStateMusicChanged() {
        //Nothing wait u override
    }

    protected open fun updateListMusic(list: List<Music>) {
        adapter.updateListMusic(list)
    }

    protected fun getMusicViewModel() = musicVM

}
