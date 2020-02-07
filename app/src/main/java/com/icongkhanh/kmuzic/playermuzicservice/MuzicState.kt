package com.icongkhanh.kmuzic.playermuzicservice

interface OnMuzicStateChangedListener {
    fun onChanged(state: MuzicState)
}

enum class MuzicState {
    PLAY,
    PAUSE,
    IDLE,
}