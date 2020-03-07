package com.icongkhanh.scaledviewpager

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ScaledFragmentPagerAdapter(val fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments = mutableListOf<Fragment>()

    /**
     * override from FragmentStatePagerAdapter, return Fragment at position to render
     * */
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    /**
     * override from FragmentStatePagerAdapter, return number of fragment
     * */
    override fun getCount(): Int {
        return fragments.size
    }

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
        notifyDataSetChanged()
    }

    fun getFragmentViewAtPosition(position: Int): View? {
        return fragments[position].view
    }

}