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
    private var progress = 0f
    private var currentMusicPlayingItem: LocalViewHoder? = null

    inner class LocalViewHoder(itemView: View) : RecyclerView.ViewHolder(itemView) {

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

        fun bindDataWithViewHolder(music: Music, position: Int) {
            this.authorName.text = music.authorName
            this.musicName.text = music.name
//            this.progressBar.progress = progress * 100

            Log.d(TAG, "Index playing: $position/$indexPlaying")
            if (position == indexPlaying) {
                this.bgProgressBar.visibility = View.VISIBLE
                currentMusicPlayingItem = this
            } else this.bgProgressBar.visibility = View.INVISIBLE

            /**
             * async get bitmap from mp3 file
             * */
            CoroutineScope(Dispatchers.Default).launch {
                Log.d(TAG, "${music.path}")
                val imgBm = BitmapUtils.getBitmapFromMusicFile(music.path)
                withContext(Dispatchers.Main) {
                    Glide.with(context)
                        .load(imgBm)
                        .into(this@LocalViewHoder.thumnail)
                }
            }

            this.itemView.setOnClickListener {
                onPressItem(music)
            }
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
        holder.bindDataWithViewHolder(item, position)
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
        Log.d(TAG, "Update index: $indexPlaying ${music.id}")
        notifyItemChanged(indexPlaying)
        notifyItemChanged(oldIndex)
//        notifyDataSetChanged()
    }

    fun updateProgress(progress: Float? = 0f) {
        this.progress = progress ?: 0f
        currentMusicPlayingItem?.progressBar?.progress = this.progress * 100
    }

    companion object {
        val TAG = "ListMusicAdapter"
    }
}
