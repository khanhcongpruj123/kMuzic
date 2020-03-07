package com.icongkhanh.kmuzic.fragments.homeviewpager


import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.icongkhanh.kmuzic.R
import com.icongkhanh.kmuzic.adapters.ListMusicAdapter
import com.icongkhanh.kmuzic.playermuzicservice.Muzic
import com.icongkhanh.kmuzic.playermuzicservice.MuzicPlayer
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class AllMusicFragment : Fragment() {

    lateinit var listMusicView: RecyclerView
    lateinit var listMusicAdapter: ListMusicAdapter

    val viewModel: AllMusicFragmentViewModel by viewModel()
    val muzicPlayer: MuzicPlayer by inject()

    companion object {
        val TAG = "AllMusicFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_all_music, container, false)

        listMusicView = view.findViewById(R.id.list_music)

        //init adapter for rcv list music
        listMusicAdapter = ListMusicAdapter(context!!)

        //play music when user press item music
        listMusicAdapter.setOnPressItem { muzic ->
            Log.d(TAG, "Path: ${muzic.path}")
            muzicPlayer.play(
                Muzic(
                    muzic.id,
                    muzic.name,
                    muzic.authorName,
                    muzic.isFavorite,
                    muzic.path
                )
            )
        }

        // set adapter for rcv list music
        listMusicView.adapter = listMusicAdapter


        // check permission if it is granted, start load music
        if (checkReadPermission()) {
            subscribeUi()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onStart() {

        super.onStart()

        // check permission if it is not granted, request permission
        if (checkReadPermission()) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }

    /**
     * handle onRequestPermissionsResult(), if result is granted then subscribeUi()
     * */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                subscribeUi()
            }
        }
    }

    override fun onStop() {
        // unsubcribe listener MuzicPlayer
        muzicPlayer.unsubscribe(this)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     *  subscribe viewmodel's data
     * */
    private fun subscribeUi() {
        Log.d(TAG, "Start Observer!")
        viewModel.listMuzic.observe(this.viewLifecycleOwner, Observer {
            Log.d(TAG, "List Music: ${it.size}")
            listMusicAdapter.updateListMuisc(it)
        })
    }

    private fun checkReadPermission(): Boolean {
        return activity?.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
    }

}
