package com.icongkhanh.kmuzic.fragments.home.homeviewpager.favouritemusic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.icongkhanh.kmuzic.R
import com.icongkhanh.kmuzic.adapters.ListMusicAdapter
import com.icongkhanh.kmuzic.utils.mapToServiceModel
import org.koin.android.viewmodel.ext.android.viewModel

class FavoriteMusicFragment : Fragment() {

    lateinit var listMusic: RecyclerView
    lateinit var adapter: ListMusicAdapter

    val vm: FavoriteMusicViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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

        adapter = ListMusicAdapter(requireContext())
        adapter.setOnPressItem {
            vm.playMusic(it.mapToServiceModel())
        }
        listMusic.adapter = adapter

        subscribeUi()
    }


    private fun subscribeUi() {
        vm.listMusic.observe(viewLifecycleOwner, Observer {
            adapter.updateListMusic(it)
        })
    }

}
