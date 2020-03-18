package com.icongkhanh.kmuzic.playermuzicservice

class NowPlaylist {

    val listMuzic: MutableList<Muzic>
    var currentPosition: Int

    init {
        listMuzic = mutableListOf()
        currentPosition = -1
    }

    fun getCurrentMuzic(): Muzic? {
        if (listMuzic.size == 0) return null
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
        val index = listMuzic.indexOfFirst { it.id == muzic.id }
        return index != -1
    }

    /**
     *  return index of music have just added
     * */
    fun addMusic(muzic: Muzic): Int {
        if (isExistedMuzic(muzic)) {
            return listMuzic.indexOf(muzic)
        } else {
            listMuzic.add(muzic)
            return listMuzic.indexOf(muzic)
        }
    }


    fun indexOfMuzic(muzic: Muzic): Int {
        return listMuzic.indexOf(muzic)
    }

    fun reset() {
    }
}
