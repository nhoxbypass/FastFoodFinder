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
    const val DOWNLOADER_BOT_EMAIL = "store_downloader@fastfoodfinder.com"
    const val DOWNLOADER_BOT_PWD = "123456789"

    const val DEFAULT_ZOOM_LEVEL = 16f
    const val DETAILED_ZOOM_LEVEL = 18f

    @JvmStatic
    val DEFAULT_MAP_TARGET = LatLng(10.773996, 106.6898035)

    const val SEARCH_STORE_PREFIX = "search-store-id_"
    const val SEARCH_STORE_PREFIX_LEN = SEARCH_STORE_PREFIX.length
}
