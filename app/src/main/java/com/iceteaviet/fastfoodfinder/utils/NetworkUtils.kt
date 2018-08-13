@file:JvmName("NetworkUtils")

package com.iceteaviet.fastfoodfinder.utils

import android.content.Context
import android.net.ConnectivityManager
import java.io.IOException

/**
 * Created by Genius Doan on 22/07/2017.
 */

val isInternetConnected: Boolean
    get() {
        val command = "ping -c 1 google.com"
        try {
            return Runtime.getRuntime().exec(command).waitFor() == 0
        } catch (e: InterruptedException) {
            e.printStackTrace()
            return false
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

    }

fun isNetworkReachable(context: Context): Boolean {
    val conMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = conMgr.activeNetworkInfo

    return networkInfo != null && networkInfo.isConnected
}
