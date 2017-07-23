package com.iceteaviet.fastfoodfinder.model.store;

import android.support.annotation.DrawableRes;

import com.google.android.gms.maps.model.LatLng;
import com.iceteaviet.fastfoodfinder.utils.DisplayUtils;
import com.iceteaviet.fastfoodfinder.utils.LocationUtils;

/**
 * Created by Genius Doan on 11/9/2016.
 */
public class StoreViewModel {

    private int id;
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
        id = store.getId();
        mStoreName = store.getTitle();
        mStoreAddress = store.getAddress();
        mDrawableLogo = DisplayUtils.getStoreLogoDrawableId(store.getType());
        mPosition = store.getPosition();
        mType = store.getType();

        //Get distance
        mStoreDistance = LocationUtils.calcDistance(currCameraPosition, store.getPosition());

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
