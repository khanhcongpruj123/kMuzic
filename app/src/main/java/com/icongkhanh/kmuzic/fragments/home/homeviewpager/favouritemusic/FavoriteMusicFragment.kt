package com.icongkhanh.kmuzic.fragments.home.homeviewpager.favouritemusic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.icongkhanh.kmuzic.R

/**
 * A simple [Fragment] subclass.
 * Use the [FavoriteMusic.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoriteMusicFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_music, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavoriteMusic.
         */
        // TODO: Rename and change types and number of parameters
        private var instance: FavoriteMusicFragment? = null

        @JvmStatic
        fun getInstance(): FavoriteMusicFragment? {
            if (instance == null) instance =
                FavoriteMusicFragment()
            return instance
        }
    }
}
