package com.icongkhanh.kmuzic.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.transition.Hold
import com.icongkhanh.kmuzic.R
import com.icongkhanh.kmuzic.adapters.HomeViewPagerAdapter
import com.icongkhanh.kmuzic.databinding.FragmentHomeBinding
import com.icongkhanh.kmuzic.playermuzicservice.MuzicState
import com.icongkhanh.kmuzic.utils.BitmapUtils
import org.koin.android.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    lateinit var viewpagerAdapter: HomeViewPagerAdapter

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    val viewModel by viewModel<HomeFragmentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = Hold()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup view
        binding.tabBar.setupWithViewPager(binding.viewpager)
        viewpagerAdapter = HomeViewPagerAdapter(childFragmentManager)
        binding.viewpager.adapter = viewpagerAdapter
        binding.musicName.isSelected = true

        // setup event

        binding.controller.setOnClickListener {
            goToNowPlaylistFragment()
        }

        binding.btnPlayOrPause.setOnClickListener {
            viewModel.playOrPause()
        }

        subscribeUi()
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun goToNowPlaylistFragment() {
        val extras = FragmentNavigatorExtras(binding.controller to "shared_element_container")
        findNavController().navigate(
            R.id.action_homeFragment_to_nowPlaylistFragment,
            null,
            null,
            extras
        )
    }

    fun subscribeUi() {

        viewModel.muzicState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                MuzicState.PLAY -> binding.btnPlayOrPause.setImageResource(R.drawable.ic_pause)
                MuzicState.PAUSE -> binding.btnPlayOrPause.setImageResource(R.drawable.ic_play_arrow)
                MuzicState.STOP -> binding.btnPlayOrPause.setImageResource(R.drawable.ic_play_arrow)
            }
        })
        viewModel.playingMusic.observe(viewLifecycleOwner, Observer { muzic ->
            if (muzic == null) {
                binding.controller.visibility = View.GONE
            } else {
                binding.controller.visibility = View.VISIBLE
                binding.musicName.text = "${muzic.name} \t\t ${muzic.authorName}"
                Log.d("HomeFragment", "On Music Changed: ${binding.musicName.text}")
                Glide.with(this).load(BitmapUtils.getBitmapFromMusicFile(muzic.path))
                    .into(binding.controllerThumnail)
            }
        })
    }
}
