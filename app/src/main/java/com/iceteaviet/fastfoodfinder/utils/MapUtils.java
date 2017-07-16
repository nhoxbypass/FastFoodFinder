package com.iceteaviet.fastfoodfinder.utils;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.iceteaviet.fastfoodfinder.R;

/**
 * Created by nhoxb on 11/16/2016.
 */
public class MapUtils {
    public static LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constant.INTERVAL);
        mLocationRequest.setFastestInterval(Constant.FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return mLocationRequest;
    }

    public static Bitmap resizeMarkerIcon(Bitmap imageBitmap, int width, int height) {
        if (width > 100)
            width = 200 - width;
        if (height > 100)
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

    public static int getLogoDrawableId(int type) {
        int id = R.drawable.logo_circlek_50;
        switch (type) {
            case Constant.TYPE_CIRCLE_K:
                id = R.drawable.logo_circlek_50;
                break;
            case Constant.TYPE_MINI_STOP:
                id = R.drawable.logo_ministop_50;
                break;
            case Constant.TYPE_FAMILY_MART:
                id = R.drawable.logo_familymart_50;
                break;
            case Constant.TYPE_BSMART:
                id = R.drawable.logo_bsmart_50;
                break;
            case Constant.TYPE_SHOP_N_GO:
                id = R.drawable.logo_shopngo_50;
                break;
        }

        return id;
    }

    public static double calcDistance(LatLng startPosition, LatLng endPosition) {
        //
        Location start = new Location("pointA");
        start.setLatitude(startPosition.latitude);
        start.setLongitude(startPosition.longitude);
        Location end = new Location("pointB");
        end.setLatitude(endPosition.latitude);
        end.setLongitude(endPosition.longitude);

        return (start.distanceTo(end) / 1000.0);
    }

    public static int getDirectionImage(String direction) {
        if (direction == null)
            return R.drawable.ic_routing_up;

        if (direction.equals("straight")) {
            return R.drawable.ic_routing_up;
        } else if (direction.equals("turn-left")) {
            return R.drawable.ic_routing_left;
        } else if (direction.equals("turn-right")) {
            return R.drawable.ic_routing_right;
        } else if (direction.equals("merge")) {
            return R.drawable.ic_routing_merge;
        } else {
            return R.drawable.ic_routing_up;
        }
    }
}
