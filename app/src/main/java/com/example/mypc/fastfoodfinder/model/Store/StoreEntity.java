package com.example.mypc.fastfoodfinder.model.Store;

import com.google.android.gms.maps.model.LatLng;

import io.realm.RealmObject;

/**
 * Created by nhoxb on 11/20/2016.
 */
public class StoreEntity extends RealmObject {

    public StoreEntity() {
    }


    public void map(Store store)
    {
        title = store.getTitle();
        latitude = Double.parseDouble(store.getLat());
        longitude = Double.parseDouble(store.getLng());
        type = store.getType();
    }

    public String getTitle() {
        return title;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LatLng getPosition()
    {
        return new LatLng(latitude, longitude);
    }

    public String getType() {
        return type;
    }

    private String title;
    private double latitude;
    private double longitude;
    String type;
}
