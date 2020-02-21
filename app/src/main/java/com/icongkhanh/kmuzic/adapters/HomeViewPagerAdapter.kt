package com.icongkhanh.kmuzic.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.icongkhanh.kmuzic.fragments.homeviewpager.AllMusicFragment
import com.icongkhanh.kmuzic.fragments.homeviewpager.FavoriteMusicFragment

class HomeViewPagerAdapter(val fm: FragmentManager)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> AllMusicFragment.getInstance()!!
            1 -> FavoriteMusicFragment.getInstance()!!
            else -> AllMusicFragment.getInstance()!!
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0 -> "All Music"
            1 -> "Favorite Music"
            else -> ""
        }
    }

    override fun getCount(): Int {
        return 2
    }

}