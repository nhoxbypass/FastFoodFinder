package com.iceteaviet.fastfoodfinder.utils

import com.google.android.gms.maps.model.LatLng

/**
 * Use class instead of interface for constant class to avoid Constant Interface Antipattern
 *
 * @see https://stackoverflow.com/questions/2659593/what-is-the-use-of-interface-constants
 *
 *
 * Created by Genius Doan on 11/11/2016.
 */

object Constant {
    //Map utils
    const val MAPS_INTERVAL = (1000 * 10).toLong()
    const val MAPS_FASTEST_INTERVAL = (1000 * 5).toLong()

    const val DOWNLOADER_BOT_EMAIL = "store_downloader@fastfoodfinder.com"
    const val DOWNLOADER_BOT_PWD = "123456789"

    const val NO_AVATAR_PLACEHOLDER_URL = "http://cdn.builtlean.com/wp-content/uploads/2015/11/all_noavatar.png.png"

    const val DEFAULT_ZOOM_LEVEL = 16f
    const val DETAILED_ZOOM_LEVEL = 18f

    @JvmStatic
    val DEFAULT_MAP_TARGET = LatLng(10.773996, 106.6898035)

    const val SEARCH_STORE_PREFIX = "$-"
}
