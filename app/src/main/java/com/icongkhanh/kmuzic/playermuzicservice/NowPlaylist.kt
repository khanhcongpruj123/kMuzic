package com.icongkhanh.kmuzic.playermuzicservice

class NowPlaylist {

    var listMuzic: MutableList<Muzic>
        set
        get() = field
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

    fun isExistedMusic(muzic: Muzic): Boolean {
        val index = listMuzic.indexOfFirst { it.id == muzic.id }
        return index != -1
    }

    /**
     *  return index of music have just added
     * */
    fun addMusic(muzic: Muzic): Int {
        return if (isExistedMusic(muzic)) {
            indexOfMuzic(muzic)
        } else {
            listMuzic.add(muzic)
            listMuzic.indexOf(muzic)
        }
    }

    fun addMusicAndPlay(muzic: Muzic): Int {
        if (!isExistedMusic(muzic)) {
            listMuzic.add(muzic)
        }
        currentPosition = indexOfMuzic(muzic)
        return currentPosition
    }


    fun indexOfMuzic(muzic: Muzic): Int {
        return listMuzic.indexOfFirst { it.id == muzic.id }
    }

    fun reset() {
        currentPosition = -1
    }

    fun goToFirst() {
        currentPosition = 0
    }
}
