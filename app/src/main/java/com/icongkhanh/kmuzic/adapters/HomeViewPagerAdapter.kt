package com.icongkhanh.kmuzic.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.icongkhanh.kmuzic.fragments.home.homeviewpager.allmusic.AllMusicFragment
import com.icongkhanh.kmuzic.fragments.home.homeviewpager.favouritemusic.FavoriteMusicFragment

class HomeViewPagerAdapter(val fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> AllMusicFragment()
            else -> FavoriteMusicFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "All Music"
            else -> "Favorite Music"
        }
    }

    override fun getCount(): Int {
        return 2
    }
}
