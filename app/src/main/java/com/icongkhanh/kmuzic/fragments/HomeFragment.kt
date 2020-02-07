package com.icongkhanh.kmuzic.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

import com.icongkhanh.kmuzic.R
import com.icongkhanh.kmuzic.adapters.HomeViewPagerAdapter
import com.icongkhanh.kmuzic.playermuzicservice.MuzicPlayer
import com.icongkhanh.kmuzic.playermuzicservice.MuzicState
import com.icongkhanh.kmuzic.playermuzicservice.OnMuzicStateChangedListener
import org.koin.android.ext.android.inject


class HomeFragment : Fragment(), OnMuzicStateChangedListener {

    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    lateinit var viewpagerAdapter: HomeViewPagerAdapter
    lateinit var btnNext: ImageButton
    lateinit var btnPrevious: ImageButton
    lateinit var btnPlayPause: ImageButton

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
        btnNext = view.findViewById(R.id.btn_next)
        btnPrevious = view.findViewById(R.id.btn_previous)
        btnPlayPause = view.findViewById(R.id.btn_play_or_pause)

        //setup view
        tabLayout.setupWithViewPager(viewPager)
        viewpagerAdapter = HomeViewPagerAdapter(childFragmentManager)
        viewPager.adapter = viewpagerAdapter

        //setup event
        muzicPlayer.setOnStateChangedListener(this)

        onChanged(muzicPlayer.muzicState)

        btnNext.setOnClickListener {
            muzicPlayer.next()
        }

        btnPlayPause.setOnClickListener {
            muzicPlayer.playOrPause()
        }

        btnPrevious.setOnClickListener {
            muzicPlayer.previous()
        }

    }

    override fun onChanged(state: MuzicState) {
        when(state) {
            MuzicState.PLAY -> btnPlayPause.setImageResource(R.drawable.ic_pause)
            MuzicState.PAUSE -> btnPlayPause.setImageResource(R.drawable.ic_play_arrow)
            MuzicState.IDLE -> btnPlayPause.setImageResource(R.drawable.ic_play_arrow)
        }
    }

}
