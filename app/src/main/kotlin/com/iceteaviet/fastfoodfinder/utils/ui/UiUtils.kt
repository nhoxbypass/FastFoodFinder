@file:JvmName("UiUtils")

package com.iceteaviet.fastfoodfinder.utils.ui

import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.animation.BounceInterpolator
import androidx.collection.LruCache
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.utils.StoreType
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by tom on 7/10/18.
 */

/**
 * Get resource id of store logo drawable
 */
fun getStoreLogoDrawableRes(type: Int): Int {
    return when (type) {
        StoreType.TYPE_CIRCLE_K -> R.drawable.logo_circlek_50
        StoreType.TYPE_MINI_STOP -> R.drawable.logo_ministop_50
        StoreType.TYPE_FAMILY_MART -> R.drawable.logo_familymart_50
        StoreType.TYPE_BSMART -> R.drawable.logo_bsmart_50
        StoreType.TYPE_SHOP_N_GO -> R.drawable.logo_shopngo_50
        else -> R.drawable.logo_circlek_50
    }
}

/**
 * Get direction image
 */
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

fun getMarkerAnimator(): ValueAnimator {
    val animator = ValueAnimator.ofFloat(0.1f, 1f)
    animator.duration = 1000
    animator.startDelay = 500
    animator.interpolator = BounceInterpolator()

    return animator
}

/**
 * Animate marker icon
 */
fun animateMarker(resources: Resources, marker: Marker?, storeType: Int) {
    if (marker == null)
        return

    val animator = getMarkerAnimator()
    animator.addUpdateListener { animation ->
        val scale = animation.animatedValue as Float
        try {
            marker.setIcon(getStoreIcon(resources, storeType, Math.round(scale * 75), Math.round(scale * 75)))
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
    }
    animator.start()
}


private var cache: LruCache<String, BitmapDescriptor> = LruCache(((Runtime.getRuntime().maxMemory() / 1024 / 8).toInt()))
// -1 is default width & height
fun getStoreIcon(resources: Resources, type: Int, width: Int, height: Int): BitmapDescriptor {
    var key = type.toString()
    if (width != -1 && height != -1)
        key = type.toString() + "-" + width + "-" + height
    synchronized(cache) {
        var result: BitmapDescriptor? = cache.get(key)
        if (result == null) {
            val id = getStoreLogoDrawableRes(type)
            val bitmap: Bitmap
            if (width != -1 && height != -1)
                bitmap = resizeMarkerBitmap(BitmapFactory.decodeResource(resources, id), width, height)
            else
                bitmap = BitmapFactory.decodeResource(resources, id)

            result = BitmapDescriptorFactory.fromBitmap(bitmap)
            cache.put(key, result)
        }

        return result!!
    }
}

/**
 * Resize marker icon
 */
fun resizeMarkerBitmap(imageBitmap: Bitmap, width: Int, height: Int): Bitmap {
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

val STORE_IMAGES = arrayListOf(R.drawable.detail_sample_food_1, R.drawable.detail_sample_food_2,
        R.drawable.detail_sample_food_3, R.drawable.detail_sample_food_4)

fun getRandomStoreImages(numb: Int): List<Int> {
    val res = ArrayList<Int>()

    val r = Random()

    for (i in 0..numb) {
        val id = STORE_IMAGES[r.nextInt(4)]
        if (!res.contains(id))
            res.add(id)
    }

    if (res.size < numb) {
        for (i in res.size..numb) {
            res.add(R.drawable.all_placeholder)
        }
    }

    return res
}

/**
 * Get resource id of store logo drawable
 */
val STORE_ICON_DRAWABLE_ARRAY: MutableList<Int> = Arrays.asList(
        R.drawable.ic_all_store24h,
        R.drawable.ic_profile_saved,
        R.drawable.ic_profile_favourite,
        R.drawable.ic_profile_list_2,
        R.drawable.ic_profile_list_4,
        R.drawable.ic_profile_list_5,
        R.drawable.ic_profile_list_6,
        R.drawable.ic_profile_list_7,
        R.drawable.ic_profile_list_8,
        R.drawable.ic_profile_list_9,
        R.drawable.ic_profile_list_10,
        R.drawable.ic_profile_list_11,
        R.drawable.ic_profile_list_3)

fun getStoreListIconDrawableRes(storeIconId: Int): Int {
    return STORE_ICON_DRAWABLE_ARRAY[storeIconId]
}