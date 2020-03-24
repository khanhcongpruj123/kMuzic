package com.icongkhanh.kmuzic.fragments.nowplaylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.material.transition.MaterialContainerTransform
import com.icongkhanh.kmuzic.R
import com.icongkhanh.kmuzic.fragments.MusicViewModel
import com.icongkhanh.kmuzic.fragments.nowplaylist.NowPlaylistContract.NowPlaylistViewEvent.Back
import com.icongkhanh.kmuzic.fragments.nowplaylist.NowPlaylistContract.NowPlaylistViewEvent.Next
import com.icongkhanh.kmuzic.fragments.nowplaylist.NowPlaylistContract.NowPlaylistViewEvent.PlayOrPause
import com.icongkhanh.kmuzic.fragments.nowplaylist.NowPlaylistContract.NowPlaylistViewEvent.Previous
import com.icongkhanh.kmuzic.fragments.nowplaylist.NowPlaylistContract.NowPlaylistViewEvent.ToggleFavorite
import com.icongkhanh.kmuzic.fragments.nowplaylist.nowplaylistviewpager.PlaylistFragment
import com.icongkhanh.kmuzic.fragments.nowplaylist.nowplaylistviewpager.ThumbnailFragment
import com.icongkhanh.kmuzic.playermuzicservice.MuzicState
import com.icongkhanh.scaledviewpager.ScaledFragmentPagerAdapter
import com.icongkhanh.scaledviewpager.ScaledTransformer
import org.koin.android.viewmodel.ext.android.sharedViewModel

class NowPlaylistFragment : Fragment() {

    companion object {
        val TAG = "NowPlaylistFragment"
    }

    private val viewmodel: MusicViewModel by sharedViewModel()

    lateinit var pager: ViewPager
    lateinit var scaledTransformer: ScaledTransformer
    lateinit var scaledPagerAdapter: ScaledFragmentPagerAdapter
    lateinit var btnPlayOrPause: ImageButton
    lateinit var btnNext: ImageButton
    lateinit var btnPrevious: ImageButton
    lateinit var seekBar: AppCompatSeekBar
    lateinit var tvMusicName: TextView
    lateinit var tvAuthorName: TextView
    lateinit var btnBack: ImageButton
    lateinit var btnLike: ImageButton

    private val viewEvent = MutableLiveData<NowPlaylistContract.NowPlaylistViewEvent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform(requireContext())
    }

    override fun onStart() {
        super.onStart()
//        viewmodel.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_now_playlist, container, false)

        btnNext = view.findViewById(R.id.btn_next)
        btnPlayOrPause = view.findViewById(R.id.btn_play_or_pause)
        btnPrevious = view.findViewById(R.id.btn_previous)
        seekBar = view.findViewById(R.id.music_progress)
        tvAuthorName = view.findViewById(R.id.author_name)
        tvMusicName = view.findViewById(R.id.music_name)
        pager = view.findViewById(R.id.pager)
        btnBack = view.findViewById(R.id.btn_back)
        btnLike = view.findViewById(R.id.btn_like)

        btnPlayOrPause.setOnClickListener {
            viewEvent.postValue(PlayOrPause)
        }

        btnNext.setOnClickListener {
            viewEvent.postValue(Next)
        }

        btnPrevious.setOnClickListener {
            viewEvent.postValue(Previous)
        }

        btnBack.setOnClickListener {
            viewEvent.postValue(Back)
        }

        btnLike.setOnClickListener {
            viewEvent.postValue(ToggleFavorite)
        }

        // init adapter pager and transformer
        scaledPagerAdapter = ScaledFragmentPagerAdapter(childFragmentManager)
        scaledTransformer = ScaledTransformer(pager, scaledPagerAdapter)

        // setup adapter
        scaledPagerAdapter.addFragment(ThumbnailFragment())
        scaledPagerAdapter.addFragment(PlaylistFragment())
        pager.adapter = scaledPagerAdapter
        pager.addOnPageChangeListener(scaledTransformer)
        pager.setPageTransformer(false, scaledTransformer)
        pager.offscreenPageLimit = 3

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_now_playlist, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // subcribe Ui
        viewmodel.stateMusic.observe(viewLifecycleOwner, Observer {
            when (it) {
                MuzicState.PLAY -> {
                    btnPlayOrPause.setImageResource(R.drawable.ic_pause_circle_filled_black_48dp)
                }
                MuzicState.PAUSE, MuzicState.STOP -> {
                    btnPlayOrPause.setImageResource(R.drawable.ic_play_circle_filled_black_48dp)
                }
            }
        })
        viewmodel.progressMusic.observe(viewLifecycleOwner, Observer {
            it?.let { seekBar.progress = (it * 100).toInt() }
        })

        viewmodel.playingMusic.observe(viewLifecycleOwner, Observer { currMusic ->
            currMusic?.let { muzic ->
                tvAuthorName.text = muzic.authorName
                tvMusicName.text = muzic.name

                val resId = if (currMusic.isFavorite) {
                    R.drawable.ic_favorite_black_36dp
                } else {
                    R.drawable.ic_favorite_border_black_36dp
                }

                btnLike.setImageResource(resId)
            }
        })

        viewEvent.observe(viewLifecycleOwner, Observer { it ->
            when (it) {
                is Next -> viewmodel.onPressNext()
                is Previous -> viewmodel.onPressPrevious()
                is PlayOrPause -> viewmodel.onPressPlayOrPause()
                is ToggleFavorite -> viewmodel.toggleFavoriteMusic()
                is Back -> findNavController().navigateUp()
            }
        })
    }

    override fun onStop() {
        super.onStop()
    }
}
