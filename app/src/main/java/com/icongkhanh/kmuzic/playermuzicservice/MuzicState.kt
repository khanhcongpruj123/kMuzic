package com.icongkhanh.kmuzic.playermuzicservice

interface OnMuzicStateChangedListener {
    fun onChanged(state: MuzicState)
}

interface OnMuzicPlayingChangedListener {
    fun onChanged(muzic: Muzic)
}

interface OnProgressChangedListener {
    fun onChanged(progress: Float)
}

enum class MuzicState {
    PLAY,
    PAUSE,
    IDLE,
}