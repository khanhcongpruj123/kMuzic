package com.icongkhanh.kmuzic.fragments.homeviewpager.allmusic


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
    lateinit var loadingView: View

    val viewModel: AllMusicFragmentViewModel by viewModel()
    val muzicPlayer: MuzicPlayer by inject()

    companion object {
        val TAG = "AllMusicFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "On Create")

        if (checkReadPermission()) subscribeUi()
        else requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "On Create View")
        val view = inflater.inflate(R.layout.fragment_all_music, container, false)

        listMusicView = view.findViewById(R.id.list_music)
        loadingView = view.findViewById(R.id.loading_view)

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

        viewModel.viewState.value?.let {
            renderUi(it)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {

        super.onStart()
        Log.d(TAG, "On Start")
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
        Log.d(TAG, "On Stop")

        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "On Destroy")
    }

    /**
     *  subscribe viewmodel's data
     * */
    private fun subscribeUi() {
        Log.d(TAG, "Subscribe UI!")
        viewModel.viewState.observe(this, Observer { state ->
            Log.d(TAG, "Call Back!")
            renderUi(state)
        })
    }

    private fun renderUi(state: AllMusicContract.ViewState) {
        //hide or show loading view
        if (state.isLoading) loadingView.visibility = View.VISIBLE
        else loadingView.visibility = View.INVISIBLE

        //update list music
        listMusicAdapter.updateListMuisc(state.music)

    }

    private fun checkReadPermission(): Boolean {
        return activity?.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

}
