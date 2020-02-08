package com.icongkhanh.kmuzic.playermuzicservice

interface OnMuzicStateChangedListener {
    fun onChanged(state: MuzicState)
}

interface OnMuzicPlayingChangedListener {
    fun onChanged(muzic: Muzic)
}

enum class MuzicState {
    PLAY,
    PAUSE,
    IDLE,
}