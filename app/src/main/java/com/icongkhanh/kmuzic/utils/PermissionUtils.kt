package com.icongkhanh.kmuzic.utils

import android.content.Context
import android.content.pm.PackageManager

object PermissionUtils {
    fun checkReadPermission(context: Context): Boolean {
        return context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
}
