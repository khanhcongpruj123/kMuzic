package com.icongkhanh.kmuzic.fragments.home.homeviewpager.allmusic

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.icongkhanh.kmuzic.R
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.fragments.BaseMusicFragment
import com.icongkhanh.kmuzic.utils.PermissionUtils.checkReadPermission
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class AllMusicFragment : BaseMusicFragment() {

    lateinit var listMusicView: RecyclerView
    lateinit var loadingView: View

    val viewModel: AllMusicFragmentViewModel by viewModel()

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

        return view
    }

    override fun getMusicRecyclerView(): RecyclerView = listMusicView

    override fun getLayoutManager(): RecyclerView.LayoutManager =
        LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

    override fun getListMusic(): LiveData<List<Music>> = viewModel.viewState.map { it.music }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        subscribeUi()
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

    /**
     *  subscribe viewmodel's data
     * */
    private fun subscribeUi() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            renderUi(state)
        })
    }

    private fun renderUi(state: AllMusicContract.ViewState) {
        // hide or show loading view
        if (state.isLoading) loadingView.visibility = View.VISIBLE
        else loadingView.visibility = View.INVISIBLE
    }
}
