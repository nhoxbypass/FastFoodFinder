@file:JvmName("NetworkUtils")

package com.iceteaviet.fastfoodfinder.utils

import android.content.Context
import com.iceteaviet.fastfoodfinder.androidext.getConnectivityManager
import com.iceteaviet.fastfoodfinder.utils.shell.AppShell
import java.io.IOException

/**
 * Created by Genius Doan on 22/07/2017.
 */

/**
 * Check internet connection access
 */
fun isInternetConnected(): Boolean {
    val command = "ping -c 1 google.com"
    try {
        return AppShell.exec(command).waitFor() == 0
    } catch (se: SecurityException) {
        return false
    } catch (ioEx: IOException) {
        return false
    } catch (interruptException: InterruptedException) {
        return false
    }
}

/**
 * Check connected to any network (but don't need to have Internet access)
 */
fun isNetworkReachable(context: Context): Boolean {
    val conMgr = context.getConnectivityManager()
    val networkInfo = conMgr?.activeNetworkInfo

    return networkInfo != null && networkInfo.isConnected
}
