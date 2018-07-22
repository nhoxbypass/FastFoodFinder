package com.iceteaviet.fastfoodfinder.data.local.store.model;

import com.google.android.gms.maps.model.LatLng;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;

import io.realm.RealmObject;

/**
 * Created by Genius Doan on 11/20/2016.
 */
public class StoreEntity extends RealmObject {

    private int type;
    private int id;
    private String title;
    private String address;
    private double latitude;
    private double longitude;
    private String telephone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void map(Store store) {
        id = store.getId();
        title = store.getTitle();
        address = store.getAddress();
        latitude = Double.parseDouble(store.getLat());
        longitude = Double.parseDouble(store.getLng());
        telephone = store.getTel();
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

    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    public int getType() {
        return type;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
