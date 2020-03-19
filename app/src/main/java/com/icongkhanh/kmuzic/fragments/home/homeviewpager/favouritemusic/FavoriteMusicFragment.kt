package com.icongkhanh.kmuzic.fragments.home.homeviewpager.favouritemusic

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
import org.koin.android.viewmodel.ext.android.viewModel

class FavoriteMusicFragment : BaseMusicFragment() {

    lateinit var listMusic: RecyclerView

    val vm: FavoriteMusicViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorite_music, container, false)

        listMusic = view.findViewById(R.id.list_music)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun getMusicRecyclerView(): RecyclerView = listMusic

    override fun getLayoutManager(): RecyclerView.LayoutManager =
        LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

    override fun getListMusic(): LiveData<List<Music>> = vm.listMusic


    private fun subscribeUi() {

    }


}
