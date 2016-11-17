package com.example.mypc.fastfoodfinder.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.example.mypc.fastfoodfinder.activity.MainActivity;

/**
 * Created by nhoxb on 11/16/2016.
 */
public class MarkerUtils {
    public static Bitmap resizeMarkerIcon(Resources resources, String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(resources,resources.getIdentifier(iconName, "drawable", MainActivity.PACKAGE_NAME));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }
}
