package com.icongkhanh.kmuzic.fragments


import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.icongkhanh.kmuzic.R
import com.icongkhanh.kmuzic.playermuzicservice.MuzicState
import com.icongkhanh.kmuzic.utils.BitmapUtils
import com.icongkhanh.kmuzic.viewmodels.NowPlaylistViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class NowPlaylistFragment : Fragment() {

    companion object {
        val TAG = "NowPlaylistFragment"
    }

    private val viewmodel: NowPlaylistViewModel by viewModel()

    //    lateinit var listMuzic: RecyclerView
//    lateinit var adapterMuzic: ListMusicAdapter
    lateinit var imgThumbnail: ImageView
    lateinit var btnPlayOrPause: ImageButton
    lateinit var btnNext: ImageButton
    lateinit var btnPrevious: ImageButton
    lateinit var seekBar: AppCompatSeekBar
    lateinit var tvMusicName: TextView
    lateinit var tvAuthorName: TextView
    lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewmodel.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_now_playlist, container, false)

//        listMuzic = view.findViewById(R.id.list_music)
        btnNext = view.findViewById(R.id.btn_next)
        btnPlayOrPause = view.findViewById(R.id.btn_play_or_pause)
        btnPrevious = view.findViewById(R.id.btn_previous)
        seekBar = view.findViewById(R.id.music_progress)
        tvAuthorName = view.findViewById(R.id.author_name)
        tvMusicName = view.findViewById(R.id.music_name)
        imgThumbnail = view.findViewById(R.id.thumbnail)
        btnBack = view.findViewById(R.id.btn_back)

        btnPlayOrPause.setOnClickListener {
            viewmodel.onPressPlayOrPause()
        }

        btnNext.setOnClickListener {
            viewmodel.onPressNext()
        }

        btnPrevious.setOnClickListener {
            viewmodel.onPressPrevious()
        }

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

//        adapterMuzic = ListMusicAdapter(context!!)
//
//        adapterMuzic.setOnPressItem {
//            viewmodel.onPressItemMuzic(it)
//        }
//
//        listMuzic.adapter = adapterMuzic
//
//        viewmodel.listMusic.observe(viewLifecycleOwner, Observer {
//            adapterMuzic.updateListMuisc(it)
//        })

        // subcribe Ui
        viewmodel.stateMuzic.observe(viewLifecycleOwner, Observer {
            when (it) {
                MuzicState.PLAY -> {
                    btnPlayOrPause.setImageResource(R.drawable.ic_pause_circle_filled_black_48dp)
                }
                MuzicState.PAUSE, MuzicState.IDLE -> {
                    btnPlayOrPause.setImageResource(R.drawable.ic_play_circle_filled_black_48dp)
                }
            }
        })
        viewmodel.progressMusic.observe(viewLifecycleOwner, Observer {
            //            Log.d(TAG, "Progess: ${it}")
            seekBar.progress = (it * 100).toInt()
        })

        viewmodel.currentPlayingMuzic.observe(viewLifecycleOwner, Observer { currMusic ->
            Log.d(TAG, "On Oserver Current Muzic: ${currMusic.name}")
            currMusic?.let { muzic ->
                tvAuthorName.text = muzic.authorName
                tvMusicName.text = muzic.name
                Glide.with(this).load(BitmapUtils.getBitmapFromMusicFile(muzic.path))
                    .into(imgThumbnail)
            }
        })

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_now_playlist, menu)
    }

    override fun onStop() {

        viewmodel.onStop()
        super.onStop()
    }

}
