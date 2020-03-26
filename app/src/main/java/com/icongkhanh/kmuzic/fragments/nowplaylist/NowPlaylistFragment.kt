package com.icongkhanh.kmuzic.fragments.nowplaylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialContainerTransform
import com.icongkhanh.kmuzic.R
import com.icongkhanh.kmuzic.databinding.FragmentNowPlaylistBinding
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

    private var _binding: FragmentNowPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewmodel: MusicViewModel by sharedViewModel()

    lateinit var scaledTransformer: ScaledTransformer
    lateinit var scaledPagerAdapter: ScaledFragmentPagerAdapter

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

        _binding = FragmentNowPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_now_playlist, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPlayOrPause.setOnClickListener {
            viewEvent.postValue(PlayOrPause)
        }

        binding.btnNext.setOnClickListener {
            viewEvent.postValue(Next)
        }

        binding.btnPrevious.setOnClickListener {
            viewEvent.postValue(Previous)
        }

        binding.btnBack.setOnClickListener {
            viewEvent.postValue(Back)
        }

        binding.btnLike.setOnClickListener {
            viewEvent.postValue(ToggleFavorite)
        }

        // init adapter pager and transformer
        scaledPagerAdapter = ScaledFragmentPagerAdapter(childFragmentManager)
        scaledTransformer = ScaledTransformer(binding.pager, scaledPagerAdapter)

        // setup adapter
        scaledPagerAdapter.addFragment(ThumbnailFragment())
        scaledPagerAdapter.addFragment(PlaylistFragment())
        binding.pager.adapter = scaledPagerAdapter
        binding.pager.addOnPageChangeListener(scaledTransformer)
        binding.pager.setPageTransformer(false, scaledTransformer)
        binding.pager.offscreenPageLimit = 3

        // subcribe Ui
        viewmodel.stateMusic.observe(viewLifecycleOwner, Observer {
            when (it) {
                MuzicState.PLAY -> {
                    binding.btnPlayOrPause.setImageResource(R.drawable.ic_pause_circle_filled_black_48dp)
                }
                MuzicState.PAUSE, MuzicState.STOP -> {
                    binding.btnPlayOrPause.setImageResource(R.drawable.ic_play_circle_filled_black_48dp)
                }
            }
        })
        viewmodel.progressMusic.observe(viewLifecycleOwner, Observer {
            it?.let { binding.musicProgress.progress = (it * 100).toInt() }
        })

        viewmodel.playingMusic.observe(viewLifecycleOwner, Observer { currMusic ->
            currMusic?.let { muzic ->
                binding.authorName.text = muzic.authorName
                binding.musicName.text = muzic.name

                val resId = if (currMusic.isFavorite) {
                    R.drawable.ic_favorite_black_36dp
                } else {
                    R.drawable.ic_favorite_border_black_36dp
                }

                binding.btnLike.setImageResource(resId)
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
