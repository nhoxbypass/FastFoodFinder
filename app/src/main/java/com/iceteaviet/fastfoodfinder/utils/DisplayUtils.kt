@file:JvmName("DisplayUtils")

package com.iceteaviet.fastfoodfinder.utils

import android.content.Context
import android.util.DisplayMetrics
import com.iceteaviet.fastfoodfinder.App

/**
 * Created by taq on 27/11/2016.
 */

/**
 * Convert Dpi to Px
 */
fun convertDpToPx(dp: Float): Float {
    val resources = App.getContext().resources
    val metrics = resources.displayMetrics
    return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

/**
 * This method converts device specific pixels to density independent pixels.
 *
 * @param px A value in px (pixels) unit. Which we need to convert into db
 * @return A float value to represent dp equivalent to px value
 */
fun convertPixelsToDp(px: Float, context: Context): Float {
    return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}