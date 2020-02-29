package com.icongkhanh.kmuzic.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.icongkhanh.kmuzic.R
import com.icongkhanh.kmuzic.adapters.HomeViewPagerAdapter
import com.icongkhanh.kmuzic.playermuzicservice.*
import com.icongkhanh.kmuzic.utils.BitmapUtils
import org.koin.android.ext.android.inject


class HomeFragment : Fragment(), OnMuzicStateChangedListener, OnMuzicPlayingChangedListener {

    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    lateinit var viewpagerAdapter: HomeViewPagerAdapter
    lateinit var imgThumbnail: ImageView
    lateinit var btnPlayPause: ImageButton
    lateinit var controllerView: View
    lateinit var tvMusicName: TextView

    val muzicPlayer: MuzicPlayer by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = view.findViewById(R.id.tab_bar)
        viewPager = view.findViewById(R.id.viewpager)
        btnPlayPause = view.findViewById(R.id.btn_play_or_pause)
        controllerView = view.findViewById(R.id.controller)
        tvMusicName = view.findViewById(R.id.music_name)
        imgThumbnail = view.findViewById(R.id.controller_thumnail)

        //setup view
        tabLayout.setupWithViewPager(viewPager)
        viewpagerAdapter = HomeViewPagerAdapter(childFragmentManager)
        viewPager.adapter = viewpagerAdapter
        tvMusicName.isSelected = true

        //setup event

        controllerView.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_nowPlaylistFragment)
        }

        btnPlayPause.setOnClickListener {
            muzicPlayer.playOrPause()
        }

        muzicPlayer.addOnStateChangedListener(this)
        muzicPlayer.addOnMuzicPlayingChangedListener(this)

        onChanged(muzicPlayer.muzicState)
        muzicPlayer.getCurrentMuzic()?.let { onChanged(it) }

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onChanged(state: MuzicState) {
        when (state) {
            MuzicState.PLAY -> btnPlayPause.setImageResource(R.drawable.ic_pause)
            MuzicState.PAUSE -> btnPlayPause.setImageResource(R.drawable.ic_play_arrow)
            MuzicState.IDLE -> btnPlayPause.setImageResource(R.drawable.ic_play_arrow)
        }
    }

    override fun onDestroy() {
        muzicPlayer.destroy(this)
        super.onDestroy()
    }

    override fun onChanged(muzic: Muzic) {
//        Log.d("HomeFragment", "On Music Changed: ")
        tvMusicName.text = "${muzic.name} \t\t ${muzic.author}"
        Log.d("HomeFragment", "On Music Changed: ${tvMusicName.text}")
        Glide.with(this).load(BitmapUtils.getBitmapFromMusicFile(muzic.path)).into(imgThumbnail)
    }
}
