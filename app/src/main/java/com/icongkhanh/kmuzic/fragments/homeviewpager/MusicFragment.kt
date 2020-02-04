package com.icongkhanh.kmuzic.fragments.homeviewpager


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

import com.icongkhanh.kmuzic.R
import com.icongkhanh.kmuzic.adapters.ListMusicAdapter
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class MusicFragment : Fragment() {

    lateinit var listMusicView: RecyclerView
    lateinit var listMusicAdapter: ListMusicAdapter

    val viewModel: MusicViewModel by viewModel()

    companion object {
        private var instance: MusicFragment? = null

        @JvmStatic
        fun getInstance() : MusicFragment? {
            if (instance == null) instance = MusicFragment()
            return instance
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listMusicView = view.findViewById(R.id.list_music)

        //setup view
        listMusicAdapter = ListMusicAdapter(context!!)
        listMusicView.adapter = listMusicAdapter

        //setup observer
        viewModel.listMuzic.observe(this.viewLifecycleOwner, Observer {
            listMusicAdapter.updateListMuisc(it)
        })

        viewModel.onStart()
    }

}
