package com.example.mypc.fastfoodfinder.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.mypc.fastfoodfinder.activity.MainActivity;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by nhoxb on 11/16/2016.
 */
public class MapUtils {
    public static Bitmap resizeMarkerIcon(Bitmap imageBitmap, int width, int height){
        if (width > 100)
            width = 200 - width;
        if (height >  100)
            height = 200 - height;

        if (width == 0)
            width = 1;
        if (height == 0)
            height = 1;


        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    public static String getLatLngString(LatLng latLng) {
        if (latLng != null)
            return String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
        else
            return null;
    }
}
