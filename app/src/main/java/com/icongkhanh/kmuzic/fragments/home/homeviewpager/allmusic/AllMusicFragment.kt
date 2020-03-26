package com.icongkhanh.kmuzic.fragments.home.homeviewpager.allmusic

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.icongkhanh.kmuzic.databinding.FragmentAllMusicBinding
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.fragments.BaseMusicFragment
import com.icongkhanh.kmuzic.uimodels.ListMusicUiModel
import com.icongkhanh.kmuzic.utils.PermissionUtils.checkReadPermission
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class AllMusicFragment : BaseMusicFragment() {


    private var _binding: FragmentAllMusicBinding? = null
    private val binding get() = _binding!!

    val viewEvent = MutableLiveData<AllMusicContract.AllMusicViewEvent>()

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
        _binding = FragmentAllMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getMusicRecyclerView(): RecyclerView = binding.listMusic

    override fun getLayoutManager(): RecyclerView.LayoutManager =
        LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

    override fun getListMusic(): LiveData<ListMusicUiModel> = viewModel.listMusic

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        subscribeUi()
    }

    override fun onMusicItemClicked(it: Music) {
        viewEvent.postValue(AllMusicContract.AllMusicViewEvent.ClickItemMusic(it))
    }

    override fun onMusicItemLongClicked(it: Music) {
        viewEvent.postValue(AllMusicContract.AllMusicViewEvent.LongClickItemMusic(it))
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
        viewModel.listMusic.observe(viewLifecycleOwner, Observer { state ->
            renderUi(state)
        })
        viewEvent.observe(viewLifecycleOwner, Observer {
            when (it) {
                is AllMusicContract.AllMusicViewEvent.ClickItemMusic -> {

                }
                is AllMusicContract.AllMusicViewEvent.LongClickItemMusic -> {

                }
            }
        })
    }

    private fun renderUi(listMusicUiModel: ListMusicUiModel) {
        // hide or show loading view
        if (listMusicUiModel.isLoading) binding.loadingView.visibility = View.VISIBLE
        else binding.loadingView.visibility = View.INVISIBLE
    }
}
