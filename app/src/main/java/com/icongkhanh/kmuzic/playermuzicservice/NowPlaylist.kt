package com.icongkhanh.kmuzic.playermuzicservice

import android.util.Log
import java.util.*

class NowPlaylist {

    val listMuzic: MutableList<Muzic>
    var currentPosition: Int

    init {
        listMuzic = mutableListOf()
        currentPosition = -1
    }

    fun getCurrentMuzic() : Muzic? {
        if (currentPosition == -1) return null
        return listMuzic[currentPosition]
    }

    fun next() {
        if (currentPosition == listMuzic.size - 1) {
            currentPosition = 0
        } else {
            currentPosition++
        }
    }

    fun previous() {
        if (currentPosition == 0) {
            currentPosition = listMuzic.size - 1
        } else {
            currentPosition--
        }
    }

    fun isExistedMuzic(muzic: Muzic): Boolean {
        return listMuzic.contains(muzic)
    }

    /**
     *  return index of music have just added
     * */
    fun addMusic(muzic: Muzic) : Int {
        if (isExistedMuzic(muzic)) {
            return listMuzic.indexOf(muzic)
        } else {
            listMuzic.add(muzic)
            return listMuzic.indexOf(muzic)
        }
    }

    fun addMusicAndPlay(muzic: Muzic) {
        if (isExistedMuzic(muzic)) {
            currentPosition = listMuzic.indexOf(muzic)
        } else {
            listMuzic.add(muzic)
            currentPosition = listMuzic.indexOf(muzic)
        }
    }

    fun indexOfMuzic(muzic: Muzic): Int {
        return listMuzic.indexOf(muzic)
    }

    fun reset() {

    }
}