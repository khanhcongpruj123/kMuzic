package com.icongkhanh.kmuzic.playermuzicservice

class NowPlaylist {

    val listMuzic: MutableList<Muzic>
    var currentPosition: Int

    init {
        listMuzic = mutableListOf()
        currentPosition = -1
    }

    fun getPlayingMuzic() : Muzic{
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
        listMuzic.add(muzic)
        return listMuzic.indexOf(muzic)
    }

    fun indexOfMuzic(muzic: Muzic): Int {
        return listMuzic.indexOf(muzic)
    }
}