@file:JvmName("BuildVersionUtils")

package com.iceteaviet.fastfoodfinder.utils

import android.os.Build

/**
 * Created by Genius Doan on 21/03/2019.
 */

fun isLolipopOrHigher(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}

