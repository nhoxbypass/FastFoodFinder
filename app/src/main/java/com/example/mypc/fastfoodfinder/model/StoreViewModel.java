package com.example.mypc.fastfoodfinder.model;

import android.location.Location;
import android.support.annotation.DrawableRes;

import com.example.mypc.fastfoodfinder.R;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by nhoxb on 11/9/2016.
 */
public class StoreViewModel {
    public String getStoreName() {
        return mStoreName;
    }

    public double getStoreDistance() {
        return mStoreDistance;
    }

    public String getStoreAddress() {
        return mStoreAddress;
    }

    public int getDrawableLogo() {
        return mDrawableLogo;
    }

    private String mStoreName;
    private double mStoreDistance;
    private String mStoreAddress;
    private int mDrawableLogo;

    public StoreViewModel(@DrawableRes int drawableLogo, String storeName, double storeDistance, String storeAddress) {
        mDrawableLogo = drawableLogo;
        mStoreName = storeName;
        mStoreDistance = storeDistance;
        mStoreAddress = storeAddress;
    }

    public StoreViewModel(Store store, LatLng currCameraPosition)
    {
        //Must refactor
        mStoreName = "Circle K";
        mStoreAddress = store.getTitle();
        mDrawableLogo = R.drawable.logo_circle_k_50;

        //
        Location start = new Location("pointA");
        start.setLatitude(currCameraPosition.latitude);
        start.setLongitude(currCameraPosition.longitude);
        Location end = new Location("pointB");
        end.setLatitude(store.getLatitude());
        end.setLongitude(store.getLongitude());

        mStoreDistance = (start.distanceTo(end)/1000.0);

    }
}
