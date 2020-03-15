package com.icongkhanh.kmuzic.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.icongkhanh.kmuzic.R
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.utils.BitmapUtils
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListMusicAdapter(val context: Context) : RecyclerView.Adapter<ListMusicAdapter.LocalViewHoder>() {

    private val listMusic = mutableListOf<Music>()
    private lateinit var onPressItem: (music: Music) -> Unit
    private var indexPlaying = -1
    private var oldIndex = indexPlaying

    class LocalViewHoder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var thumnail: ImageView
        var musicName: TextView
        var authorName: TextView
        var progressBar: CircularProgressBar
        var bgProgressBar: View

        init {
            thumnail = itemView.findViewById(R.id.item_music_thumnail)
            musicName = itemView.findViewById(R.id.music_name)
            authorName = itemView.findViewById(R.id.author_name)
            progressBar = itemView.findViewById(R.id.progress_bar)
            bgProgressBar = itemView.findViewById(R.id.bg_progress_bar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHoder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_music_layout, parent, false)
        return LocalViewHoder(view)
    }

    override fun getItemCount(): Int {
        return listMusic.size
    }

    fun updateListMuisc(list: List<Music>) {
        listMusic.clear()
        listMusic.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: LocalViewHoder, position: Int) {
        val item = listMusic[position]

        holder.authorName.text = item.authorName
        holder.musicName.text = item.name

        Log.d(TAG, "Index playing: ${position}/${indexPlaying}")
        if (position == indexPlaying) holder.bgProgressBar.visibility = View.VISIBLE
        else holder.bgProgressBar.visibility = View.INVISIBLE

        /**
         * async get bitmap from mp3 file
         * */
        CoroutineScope(Dispatchers.Default).launch {
            val imgBm = BitmapUtils.getBitmapFromMusicFile(item.path)
            withContext(Dispatchers.Main) {
                Glide.with(context)
                    .asBitmap()
                    .load(imgBm)
                    .into(holder.thumnail)
            }
        }

        holder.itemView.setOnClickListener {
            onPressItem(item)
        }
    }

    fun setOnPressItem(callback: (music: Music) -> Unit) {
        onPressItem = callback
    }

    fun updatePlayingMusic(music: Music) {
        oldIndex = indexPlaying
        indexPlaying = listMusic.indexOfFirst {
            val isTrue = it.id == music.id
            isTrue
        }
        Log.d(TAG, "Update index: ${indexPlaying}")
        notifyItemChanged(indexPlaying)
        notifyItemChanged(oldIndex)
    }

    companion object {
        val TAG = "ListMusicAdapter"
    }

}