package com.icongkhanh.kmuzic.fragments.nowplaylist.nowplaylistviewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.icongkhanh.kmuzic.R
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.fragments.BaseMusicFragment

class PlaylistFragment() : BaseMusicFragment() {

    lateinit var listMusic: RecyclerView
    override fun getMusicRecyclerView(): RecyclerView = listMusic

    override fun getLayoutManager(): RecyclerView.LayoutManager =
        LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

    override fun getListMusic(): LiveData<List<Music>> = getMusicViewModel().nowplaylist

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_playlist, container, false)
        listMusic = view.findViewById(R.id.list_music)
        return view
    }

    companion object {
        val TAG = this::class.java.simpleName
    }
}
