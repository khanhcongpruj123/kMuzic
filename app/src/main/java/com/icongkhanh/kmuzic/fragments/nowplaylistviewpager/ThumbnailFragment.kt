package com.icongkhanh.kmuzic.fragments.nowplaylistviewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.icongkhanh.kmuzic.R

/**
 * A simple [Fragment] subclass.
 */
class ThumbnailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_thumbnail, container, false)
    }

}
