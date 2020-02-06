package com.icongkhanh.kmuzic.playmuzicservice

interface OnMuzicStateChangedListener {
    fun onChanged(state: MuzicState)
}

enum class MuzicState {
    PLAY,
    PAUSE,
    IDLE,
}