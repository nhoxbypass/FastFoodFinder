package com.iceteaviet.fastfoodfinder.utils.ui

/**
 * Created by tom on 2019-07-07.
 */
interface NotiManager {
    fun showStoreSyncStatusNotification(message: String, title: String)
    fun showStoreSyncProgressStatusNotification(message: String, title: String)
}