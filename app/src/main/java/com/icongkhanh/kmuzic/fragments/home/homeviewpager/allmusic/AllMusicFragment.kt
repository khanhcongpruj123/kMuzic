package com.icongkhanh.kmuzic.fragments.home.homeviewpager.allmusic

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
import com.icongkhanh.kmuzic.utils.PermissionUtils.checkReadPermission
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class AllMusicFragment : Fragment() {

    lateinit var listMusicView: RecyclerView
    lateinit var listMusicAdapter: ListMusicAdapter
    lateinit var loadingView: View

    val viewModel: AllMusicFragmentViewModel by viewModel()
    val muzicPlayer: MuzicPlayer by inject()

    companion object {
        val TAG = "AllMusicFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (checkReadPermission(context!!))
        else requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_music, container, false)

        listMusicView = view.findViewById(R.id.list_music)
        loadingView = view.findViewById(R.id.loading_view)

        // init adapter for rcv list music
        listMusicAdapter = ListMusicAdapter(context!!)

        // play music when user press item music
        listMusicAdapter.setOnPressItem { muzic ->
//            Log.d(TAG, "Path: ${muzic.path}")
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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeUi()
    }

    override fun onStart() {

        super.onStart()
        viewModel.onStart()
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

        /**
         * if read permission is granted, viewmodel load all music
         * */
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel._loadAllMusic()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     *  subscribe viewmodel's data
     * */
    private fun subscribeUi() {
        Log.d(TAG, "Subscribe UI!")
        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            renderUi(state)
        })
        viewModel.playingMusic.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "Update Playing Music!")
            listMusicAdapter.updatePlayingMusic(it)
        })

        viewModel.progressMusic.observe(viewLifecycleOwner, Observer {
            listMusicAdapter.updateProgress(it)
        })
    }

    private fun renderUi(state: AllMusicContract.ViewState) {
        // hide or show loading view
        if (state.isLoading) loadingView.visibility = View.VISIBLE
        else loadingView.visibility = View.INVISIBLE

        // update list music
        listMusicAdapter.updateListMuisc(state.music)
    }
}
