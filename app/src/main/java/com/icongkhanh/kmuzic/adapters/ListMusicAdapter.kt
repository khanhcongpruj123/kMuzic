package com.icongkhanh.kmuzic.adapters

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.icongkhanh.kmuzic.R
import com.icongkhanh.kmuzic.data.executor.ThreadPoolManager
import com.icongkhanh.kmuzic.domain.models.Music
import com.icongkhanh.kmuzic.fragments.MusicViewModel
import com.icongkhanh.kmuzic.utils.BitmapUtils
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class ListMusicAdapter(val context: Context) : ListAdapter<Music, ListMusicAdapter.LocalViewHoder>(MusicDiff) {

    private lateinit var onPressItem: (music: Music) -> Unit
    private lateinit var onLongPressItem: (music: Music) -> Unit
    private var indexPlaying = -1
    private var oldIndex = indexPlaying
    private var progress = 0f
    private var currentMusicPlayingItem: LocalViewHoder? = null

    val handlerThread: HandlerThread
    val handler: Handler

    init {
        handlerThread = HandlerThread("load-image")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

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
            this.progressBar.post {
                this.progressBar.progress = progress * 100
            }

            if (position == indexPlaying) {
                this.bgProgressBar.visibility = View.VISIBLE
                currentMusicPlayingItem = this
            } else this.bgProgressBar.visibility = View.INVISIBLE

            handler.postDelayed({
                val imgBm = BitmapUtils.getBitmapFromMusicFile(music.path)
                thumnail.postDelayed({
                    Glide.with(context)
                        .load(imgBm)
                        .into(this@LocalViewHoder.thumnail)
                }, 0)
            }, 0)

            this.itemView.setOnClickListener {
                onPressItem(music)
            }

            this.itemView.setOnLongClickListener {
                onLongPressItem(music)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHoder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_music_layout, parent, false)
        return LocalViewHoder(view)
    }


    fun setOnPressItem(callback: (music: Music) -> Unit) {
        onPressItem = callback
    }

    fun updatePlayingMusic(music: Music?) {
//        if (music == null) return
//        oldIndex = indexPlaying
//        indexPlaying = get.indexOfFirst {
//            val isTrue = it.id == music.id
//            isTrue
//        }
//        notifyItemChanged(oldIndex)
//        notifyItemChanged(indexPlaying)
    }

    fun updateProgress(progress: Float? = 0f) {
        this.progress = progress ?: 0f
        currentMusicPlayingItem?.progressBar?.progress = this.progress * 100
    }

    fun setOnLongPressItem(callback: (music: Music) -> Unit) {
        onLongPressItem = callback
    }

    companion object {
        val TAG = "ListMusicAdapter"
    }

    object MusicDiff : DiffUtil.ItemCallback<Music>() {
        override fun areItemsTheSame(oldItem: Music, newItem: Music): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Music, newItem: Music): Boolean {
            return oldItem.id == newItem.id
        }

    }

    override fun onBindViewHolder(holder: LocalViewHoder, position: Int) {
        holder.bindDataWithViewHolder(getItem(position), position)
    }
}
