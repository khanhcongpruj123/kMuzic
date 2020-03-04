package com.icongkhanh.kmuzic.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.icongkhanh.kmuzic.R
import com.icongkhanh.kmuzic.domain.models.Muzic
import com.icongkhanh.kmuzic.utils.BitmapUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListMusicAdapter(val context: Context) : RecyclerView.Adapter<ListMusicAdapter.LocalViewHoder>() {

    private val listMusic = mutableListOf<Muzic>()
    private lateinit var onPressItem: (muzic: Muzic) -> Unit

    class LocalViewHoder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var thumnail: ImageView
        var musicName: TextView
        var authorName: TextView

        init {
            thumnail = itemView.findViewById(R.id.item_music_thumnail)
            musicName = itemView.findViewById(R.id.music_name)
            authorName = itemView.findViewById(R.id.author_name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHoder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_music_layout, parent, false)
        return LocalViewHoder(view)
    }

    override fun getItemCount(): Int {
        return listMusic.size
    }

    fun updateListMuisc(list: List<Muzic>) {
        listMusic.clear()
        listMusic.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: LocalViewHoder, position: Int) {
        val item = listMusic[position]

        holder.authorName.text = item.authorName
        holder.musicName.text = item.name

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

    fun setOnPressItem(callback: (muzic: Muzic) -> Unit) {
        onPressItem = callback
    }

}