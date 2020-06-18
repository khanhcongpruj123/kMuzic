package com.icongkhanh.kmuzic.fragments.home.homeviewpager.favouritemusic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.icongkhanh.kmuzic.databinding.FragmentFavoriteMusicBinding
import com.icongkhanh.kmuzic.fragments.BaseMusicFragment
import com.icongkhanh.kmuzic.uimodels.ListMusicUiModel
import org.koin.android.viewmodel.ext.android.viewModel

class FavoriteMusicFragment : BaseMusicFragment() {

    private var _binding: FragmentFavoriteMusicBinding? = null
    private val binding get() = _binding!!

    val vm: FavoriteMusicViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeUi()
    }

    override fun onResume() {
        super.onResume()

        vm.loadMusic()
    }

    override fun getMusicRecyclerView(): RecyclerView = binding.listMusic

    override fun getLayoutManager(): RecyclerView.LayoutManager =
        LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

    override fun getListMusic(): LiveData<ListMusicUiModel> = vm.listMusic


    private fun subscribeUi() {

    }


}
