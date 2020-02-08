package com.icongkhanh.kmuzic.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsSeekBar
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

import com.icongkhanh.kmuzic.R
import com.icongkhanh.kmuzic.adapters.ListMusicAdapter
import com.icongkhanh.kmuzic.playermuzicservice.Muzic
import com.icongkhanh.kmuzic.playermuzicservice.MuzicState
import com.icongkhanh.kmuzic.viewmodels.NowPlaylistViewModel
import org.koin.android.ext.android.inject

/**
 * A simple [Fragment] subclass.
 */
class NowPlaylistFragment : Fragment() {

    companion object {
        val TAG = this.javaClass.simpleName
    }

    private val viewmodel: NowPlaylistViewModel by inject()

    lateinit var listMuzic: RecyclerView
    lateinit var adapterMuzic: ListMusicAdapter
    lateinit var btnPlayOrPause: ImageButton
    lateinit var btnNext: ImageButton
    lateinit var btnPrevious: ImageButton
    lateinit var seekBar: AppCompatSeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewmodel.listMusic.observe(this, Observer {
            adapterMuzic.updateListMuisc(it)
        })

        viewmodel.stateMuzic.observe(this, Observer {
            when(it) {
                MuzicState.PLAY -> {
                    btnPlayOrPause.setImageResource(R.drawable.ic_pause_circle_filled_black_48dp)
                }
                MuzicState.PAUSE, MuzicState.IDLE -> {
                    btnPlayOrPause.setImageResource(R.drawable.ic_play_circle_filled_black_48dp)
                }
            }
        })
        viewmodel.progressMusic.observe(this, Observer {
            Log.d(TAG, "Progess: ${it}")
            seekBar.progress = (it * 100).toInt()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_now_playlist, container, false)

        listMuzic = view.findViewById(R.id.list_music)
        btnNext = view.findViewById(R.id.btn_next)
        btnPlayOrPause = view.findViewById(R.id.btn_play_or_pause)
        btnPrevious = view.findViewById(R.id.btn_previous)
        seekBar = view.findViewById(R.id.music_progress)

        btnPlayOrPause.setOnClickListener {
            viewmodel.onPressPlayOrPause()
        }

        btnNext.setOnClickListener {
            viewmodel.onPressNext()
        }

        btnPrevious.setOnClickListener {
            viewmodel.onPressPrevious()
        }

        adapterMuzic = ListMusicAdapter(context!!)
        listMuzic.adapter = adapterMuzic

        return view
    }

    override fun onStart() {
        super.onStart()

        viewmodel.onStart()
    }

}
