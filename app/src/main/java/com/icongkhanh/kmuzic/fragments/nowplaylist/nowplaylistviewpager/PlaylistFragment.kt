package com.icongkhanh.kmuzic.fragments.nowplaylist.nowplaylistviewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.icongkhanh.kmuzic.databinding.FragmentPlaylistBinding
import com.icongkhanh.kmuzic.fragments.BaseMusicFragment
import com.icongkhanh.kmuzic.uimodels.ListMusicUiModel

class PlaylistFragment() : BaseMusicFragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getMusicRecyclerView(): RecyclerView = binding.listMusic

    override fun getLayoutManager(): RecyclerView.LayoutManager =
        LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

    override fun getListMusic(): LiveData<ListMusicUiModel> = getMusicViewModel().nowplaylist

    companion object {
        val TAG = this::class.java.simpleName
    }
}
