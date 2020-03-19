package com.icongkhanh.kmuzic.fragments.nowplaylist.nowplaylistviewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.icongkhanh.kmuzic.R
import com.icongkhanh.kmuzic.fragments.MusicViewModel
import com.icongkhanh.kmuzic.utils.BitmapUtils
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class ThumbnailFragment() : Fragment() {

    val nowplaylistVM by sharedViewModel<MusicViewModel>()
    lateinit var thumbnail: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_thumbnail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        thumbnail = view.findViewById(R.id.thumbnail)

        nowplaylistVM.playingMusic.observe(viewLifecycleOwner, Observer {
            lifecycleScope.launch {
                val img = BitmapUtils.getBitmapFromMusicFile(it.path)
                img?.let { bm ->
                    Glide.with(this@ThumbnailFragment)
                        .load(bm)
                        .into(thumbnail)
                }
            }
        })
    }

    companion object {
        val TAG = this::class.java.simpleName
    }
}
