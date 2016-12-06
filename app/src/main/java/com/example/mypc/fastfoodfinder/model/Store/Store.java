package com.example.mypc.fastfoodfinder.model.Store;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.io.Serializable;

/**
 * Created by nhoxb on 11/10/2016.
 */
public class Store implements Serializable {

    //Type
    //0: circle_k
    //1: logo_red_ministop
    //2: family mart
    //3: logo_red_bsmart
    //4: shop n go

    @Exclude
    int type;
    @PropertyName("id")
    private int id;
    @PropertyName("title")
    private String title;
    @PropertyName("address")
    private String address;
    @PropertyName("lat")
    private String lat;
    @PropertyName("lng")
    private String lng;
    @PropertyName("tel")
    private String tel;

    public Store() {
    }

    public Store(String title, String address, String lat, String lng, String tel, int type) {
        this.title = title;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.tel = tel;
        this.type = type;
    }

    public Store(StoreEntity entity) {
        title = entity.getTitle();
        address = entity.getAddress();
        lat = String.valueOf(entity.getLatitude());
        lng = String.valueOf(entity.getLongitude());
        tel = entity.getTelephone();
        type = entity.getType();
    }

    public Store(StoreViewModel viewModel) {
        title = viewModel.getStoreName();
        lat = String.valueOf(viewModel.getPosition().latitude);
        lng = String.valueOf(viewModel.getPosition().longitude);
        tel = "3835 3193";
        type = viewModel.getType();
        address = viewModel.getStoreAddress();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Exclude
    public LatLng getPosition() {
        return new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
    }

    public String getTitle() {
        return title;
    }

    @PropertyName("title")
    public void setTitle(String title) {
        this.title = title;
    }

    public String getLat() {
        return lat;
    }

    @PropertyName("lat")
    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    @PropertyName("lng")
    public void setLng(String lng) {
        this.lng = lng;
    }

    @Exclude
    public int getType() {
        return type;
    }

    @Exclude
    public void setType(int type) {
        this.type = type;
    }

    public String getTel() {
        return tel;
    }

    @PropertyName("tel")
    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
