package com.icongkhanh.kmuzic.fragments.nowplaylist.nowplaylistviewpager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.icongkhanh.kmuzic.R
import com.icongkhanh.kmuzic.adapters.ListMusicAdapter
import com.icongkhanh.kmuzic.fragments.nowplaylist.NowPlaylistViewModel
import com.icongkhanh.kmuzic.playermuzicservice.Muzic
import org.koin.android.viewmodel.ext.android.sharedViewModel

class PlaylistFragment() : Fragment() {

    lateinit var listMusic: RecyclerView
    lateinit var adapter: ListMusicAdapter
    val vm by sharedViewModel<NowPlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listMusic = view.findViewById(R.id.list_music)

        adapter = ListMusicAdapter(requireContext())
        adapter.setOnPressItem {
            vm.play(
                Muzic(
                    it.id,
                    it.name,
                    it.authorName,
                    it.isFavorite,
                    it.path
                )
            )
        }
        listMusic.adapter = adapter

        vm.listMusic.observe(viewLifecycleOwner, Observer {
            adapter.updateListMuisc(it)
        })

        vm.currentPlayingMusic.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "Music: ${it}")
            adapter.updatePlayingMusic(it)
        })

        vm.progressMusic.observe(viewLifecycleOwner, Observer {
            adapter.updateProgress(it)
        })
    }

    companion object {
        val TAG = this::class.java.simpleName
    }
}
