@file:JvmName("DisplayUtils")

package com.iceteaviet.fastfoodfinder.utils

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.DisplayMetrics
import com.iceteaviet.fastfoodfinder.App

/**
 * Created by taq on 27/11/2016.
 */

private const val BITMAP_SCALE = 0.4f
private const val BLUR_RADIUS = 7.5f

/**
 * Blur image
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun blur(context: Context, image: Bitmap): Bitmap {
    val width = Math.round(image.width * BITMAP_SCALE)
    val height = Math.round(image.height * BITMAP_SCALE)

    val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
    val outputBitmap = Bitmap.createBitmap(inputBitmap)

    val rs = RenderScript.create(context)
    val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
    val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
    val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
    theIntrinsic.setRadius(BLUR_RADIUS)
    theIntrinsic.setInput(tmpIn)
    theIntrinsic.forEach(tmpOut)
    tmpOut.copyTo(outputBitmap)

    return outputBitmap
}

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


/**
 * Resize marker icon
 */
fun resizeMarkerIcon(imageBitmap: Bitmap, width: Int, height: Int): Bitmap {
    var width = width
    var height = height

    if (width > 100)
        width = 200 - width
    if (height > 100)
        height = 200 - height

    if (width == 0)
        width = 1
    if (height == 0)
        height = 1


    return Bitmap.createScaledBitmap(imageBitmap, width, height, false)
}