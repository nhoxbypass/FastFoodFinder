package com.iceteaviet.fastfoodfinder.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by nhoxb on 11/8/2016.
 */
public class PermissionUtils {
    public static final int REQUEST_LOCATION = 1000;

    public static void requestLocaiton(Activity context) {
        if (!isLocationPermissionGranted(context)) {
            ActivityCompat.requestPermissions(context, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);
        }
    }

    public static boolean isLocationPermissionGranted(Context context) {
        return !(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED);
    }
}
