package com.iceteaviet.fastfoodfinder.androidext

import android.app.NotificationManager
import android.app.SearchManager
import android.content.Context
import android.hardware.SensorManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.view.inputmethod.InputMethodManager

/**
 * Created by tom on 2019-07-07.
 */

fun Context.getConnectivityManager(): ConnectivityManager? {
    val service = getSystemService(Context.CONNECTIVITY_SERVICE)
    return if (service is ConnectivityManager) service else null
}

fun Context.getLocationManager(): LocationManager? {
    val service = getSystemService(Context.LOCATION_SERVICE)
    return if (service is LocationManager) service else null
}

fun Context.getNotificationManager(): NotificationManager? {
    val service = getSystemService(Context.NOTIFICATION_SERVICE)
    return if (service is NotificationManager) service else null
}

fun Context.getSensorManager(): SensorManager? {
    val service = getSystemService(Context.SENSOR_SERVICE)
    return if (service is SensorManager) service else null
}

fun Context.getInputMethodManager(): InputMethodManager? {
    val service = getSystemService(Context.INPUT_METHOD_SERVICE)
    return if (service is InputMethodManager) service else null
}

fun Context.getSearchManager(): SearchManager? {
    val service = getSystemService(Context.SEARCH_SERVICE)
    return if (service is SearchManager) service else null
}