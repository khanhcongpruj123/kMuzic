package com.icongkhanh.kmuzic.playmuzicservice

class NowPlaylist {

    val listMuzic: MutableList<Muzic>
    var currentPosition: Int

    init {
        listMuzic = mutableListOf()
        currentPosition = 0
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

    /**
     *  return index of music have just added
     * */
    fun addMusic(muzic: Muzic) : Int {
        listMuzic.add(muzic)
        return listMuzic.indexOf(muzic)
    }
}