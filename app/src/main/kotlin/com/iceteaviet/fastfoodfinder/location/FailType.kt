package com.iceteaviet.fastfoodfinder.location

import androidx.annotation.IntDef


/**
 * Created by tom on 2019-05-01.
 */
@IntDef(FailType.UNKNOWN, FailType.TIMEOUT, FailType.PERMISSION_DENIED, FailType.NETWORK_NOT_AVAILABLE, FailType.GOOGLE_PLAY_SERVICES_NOT_AVAILABLE, FailType.GOOGLE_PLAY_SERVICES_CONNECTION_FAIL, FailType.GOOGLE_PLAY_SERVICES_SETTINGS_DIALOG, FailType.GOOGLE_PLAY_SERVICES_SETTINGS_DENIED, FailType.VIEW_DETACHED, FailType.VIEW_NOT_REQUIRED_TYPE)
@Retention(AnnotationRetention.SOURCE)
annotation class FailType {
    companion object {

        const val UNKNOWN = -1
        const val TIMEOUT = 1
        const val PERMISSION_DENIED = 2
        const val NETWORK_NOT_AVAILABLE = 3
        const val GOOGLE_PLAY_SERVICES_NOT_AVAILABLE = 4
        const val GOOGLE_PLAY_SERVICES_CONNECTION_FAIL = 5
        const val GOOGLE_PLAY_SERVICES_SETTINGS_DIALOG = 6
        const val GOOGLE_PLAY_SERVICES_SETTINGS_DENIED = 7
        const val VIEW_DETACHED = 8
        const val VIEW_NOT_REQUIRED_TYPE = 9
    }
}