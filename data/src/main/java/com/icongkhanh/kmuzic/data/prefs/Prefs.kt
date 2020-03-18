package com.icongkhanh.kmuzic.data.prefs

import android.content.Context
import android.content.SharedPreferences

class Prefs(val context: Context) {

    val PREFS_NAME = "kMuzic"
    val IS_FIRST_TIME_OPEN_APP = "is_first_open_app"

    fun isFirstTimeOpenApp(): Boolean {
        val prefs = getPrefs()
        return prefs.getBoolean(IS_FIRST_TIME_OPEN_APP, true)
    }

    fun setIsFirstTimeOpenApp(b: Boolean) {
        val editor = getPrefs().edit()
        editor.putBoolean(IS_FIRST_TIME_OPEN_APP, false)
        editor.apply()
    }

    private fun getPrefs(): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}
