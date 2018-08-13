@file:JvmName("PermissionUtils")

package com.iceteaviet.fastfoodfinder.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Created by Genius Doan on 11/8/2016.
 */

const val REQUEST_LOCATION = 1001
const val REQUEST_CALL_PHONE = 1002
const val REQUEST_CAMERA = 1003

fun requestLocationPermission(activity: Activity) {
    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_LOCATION)
}

fun requestLocationPermission(fragment: Fragment) {
    fragment.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_LOCATION)
}

fun isLocationPermissionGranted(context: Context): Boolean {
    return !(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
}

fun requestCallPhonePermission(fragment: Fragment) {
    fragment.requestPermissions(arrayOf(Manifest.permission.CALL_PHONE),
            REQUEST_CALL_PHONE)
}

fun isCallPhonePermissionGranted(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
}

fun requestCameraPermission(activity: Activity) {
    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA),
            REQUEST_CAMERA)
}

fun isCameraPermissionGranted(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
}
