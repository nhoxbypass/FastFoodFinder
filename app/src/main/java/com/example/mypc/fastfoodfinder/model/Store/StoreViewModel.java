package com.example.mypc.fastfoodfinder.model.Store;

import android.support.annotation.DrawableRes;

import com.example.mypc.fastfoodfinder.utils.MapUtils;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by nhoxb on 11/9/2016.
 */
public class StoreViewModel {
    private String mStoreName;
    private double mStoreDistance;
    private String mStoreAddress;
    private int mDrawableLogo;
    private LatLng mPosition;
    private int mType;

    public StoreViewModel(@DrawableRes int drawableLogo, String storeName, double storeDistance, String storeAddress, LatLng position, int type) {
        mDrawableLogo = drawableLogo;
        mStoreName = storeName;
        mStoreDistance = storeDistance;
        mStoreAddress = storeAddress;
        mPosition = position;
        mType = type;
    }
    public StoreViewModel(Store store, LatLng currCameraPosition) {
        //Must refactor
        mStoreName = store.getTitle();
        mStoreAddress = store.getAddress();
        mDrawableLogo = MapUtils.getLogoDrawableId(store.getType());
        mPosition = store.getPosition();
        mType = store.getType();

        //Get distance
        mStoreDistance = MapUtils.calcDistance(currCameraPosition, store.getPosition());

    }

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

    public int getType() {
        return mType;
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    public LatLng getPosition() {
        return mPosition;
    }
}
