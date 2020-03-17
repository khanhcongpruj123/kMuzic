package com.icongkhanh.scaledviewpager

import android.view.View
import androidx.viewpager.widget.ViewPager

class ScaledTransformer(val pager: ViewPager, val adapter: ScaledFragmentPagerAdapter) :
    ViewPager.PageTransformer, ViewPager.OnPageChangeListener {

    private var mLastOffset = 0f

    override fun transformPage(page: View, position: Float) {

    }

    companion object {
        val TAG = "ScaledTransformer"
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        var realCurrentPosition: Int
        var nextPosition: Int
        var realOffset: Float
        val goingLeft: Boolean = mLastOffset > positionOffset

        // If we're going backwards, onPageScrolled receives the last position
        // instead of the current one
        // If we're going backwards, onPageScrolled receives the last position
        // instead of the current one
        if (goingLeft) {
            realCurrentPosition = position + 1
            nextPosition = position
            realOffset = 1 - positionOffset
        } else {
            nextPosition = position + 1
            realCurrentPosition = position
            realOffset = positionOffset
        }

        // Avoid crash on overscroll
        if (nextPosition > adapter.getCount() - 1
            || realCurrentPosition > adapter.getCount() - 1
        ) {
            return
        }

        val currentFragmentView = adapter.getFragmentViewAtPosition(realCurrentPosition)

        // This might be null if a fragment is being used
        // and the views weren't created yet
        // This might be null if a fragment is being used
        // and the views weren't created yet
        if (currentFragmentView != null) {
//            currentFragmentView.scaleX = 0.8f
            currentFragmentView.scaleY = 0.8f + 0.2f * (1 - realOffset)
        }

        val nextFragmentView = adapter.getFragmentViewAtPosition(nextPosition)

        // We might be scrolling fast enough so that the next (or previous) card
        // was already destroyed or a fragment might not have been created yet
        // We might be scrolling fast enough so that the next (or previous) card
        // was already destroyed or a fragment might not have been created yet
        if (nextFragmentView != null) {
//            nextFragmentView.scaleX = 0.8f
            nextFragmentView.scaleY = 0.8f + 0.2f * realOffset
        }

        mLastOffset = positionOffset
    }

    override fun onPageSelected(position: Int) {

    }
}