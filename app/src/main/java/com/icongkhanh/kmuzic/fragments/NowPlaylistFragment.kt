package com.icongkhanh.kmuzic.fragments


import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.icongkhanh.kmuzic.R
import com.icongkhanh.kmuzic.adapters.ListMusicAdapter
import com.icongkhanh.kmuzic.playermuzicservice.MuzicState
import com.icongkhanh.kmuzic.viewmodels.NowPlaylistViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class NowPlaylistFragment : Fragment() {

    companion object {
        val TAG = this::class.java.simpleName
    }

    private val viewmodel: NowPlaylistViewModel by viewModel()

    lateinit var listMuzic: RecyclerView
    lateinit var adapterMuzic: ListMusicAdapter
    lateinit var btnPlayOrPause: ImageButton
    lateinit var btnNext: ImageButton
    lateinit var btnPrevious: ImageButton
    lateinit var seekBar: AppCompatSeekBar
    lateinit var tvMusicName: TextView
    lateinit var tvAuthorName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewmodel.listMusic.observe(this, Observer {
            adapterMuzic.updateListMuisc(it)
        })

        viewmodel.stateMuzic.observe(this, Observer {
            when (it) {
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

        viewmodel.currentPlayingPos.observe(this, Observer {
            val currMusic = viewmodel.listMusic.value?.get(it)
            Log.d(TAG, "On Oserver Current Muzic: ${currMusic?.name}")
            currMusic?.let { muzic ->
                tvAuthorName.text = muzic.authorName
                tvMusicName.text = muzic.name
            }
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
        adapterMuzic.setOnPressItem {
            viewmodel.onPressItemMuzic(it)
        }
        listMuzic.adapter = adapterMuzic

        return view
    }

    override fun onStart() {
        super.onStart()

        viewmodel.onStart()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_now_playlist, menu)
    }

}
