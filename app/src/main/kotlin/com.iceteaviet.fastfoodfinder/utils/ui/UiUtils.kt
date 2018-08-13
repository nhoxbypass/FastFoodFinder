@file:JvmName("UiUtils")

package com.iceteaviet.fastfoodfinder.utils.ui

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.view.animation.BounceInterpolator

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.utils.Constant
import com.iceteaviet.fastfoodfinder.utils.resizeMarkerIcon

/**
 * Created by tom on 7/10/18.
 */

fun getStoreLogoDrawableId(type: Int): Int {
    when (type) {
        Constant.TYPE_CIRCLE_K -> return R.drawable.logo_circlek_50
        Constant.TYPE_MINI_STOP -> return R.drawable.logo_ministop_50
        Constant.TYPE_FAMILY_MART -> return R.drawable.logo_familymart_50
        Constant.TYPE_BSMART -> return R.drawable.logo_bsmart_50
        Constant.TYPE_SHOP_N_GO -> return R.drawable.logo_shopngo_50
        else -> return R.drawable.logo_circlek_50
    }
}

fun getDirectionImage(direction: String?): Int {
    if (direction == null)
        return R.drawable.ic_routing_up

    return if (direction == "straight") {
        R.drawable.ic_routing_up
    } else if (direction == "turn-left") {
        R.drawable.ic_routing_left
    } else if (direction == "turn-right") {
        R.drawable.ic_routing_right
    } else if (direction == "merge") {
        R.drawable.ic_routing_merge
    } else {
        R.drawable.ic_routing_up
    }
}

fun animateMarker(bitmap: Bitmap, marker: Marker?) {
    if (marker == null)
        return

    val animator = ValueAnimator.ofFloat(0.1f, 1f)
    animator.duration = 1000
    animator.startDelay = 500
    animator.interpolator = BounceInterpolator()

    animator.addUpdateListener { animation ->
        val scale = animation.animatedValue as Float
        try {
            // TODO: Optimize .fromBitmap & resize icons
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMarkerIcon(bitmap, Math.round(scale * 75), Math.round(scale * 75))))
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
    }
    animator.start()
}