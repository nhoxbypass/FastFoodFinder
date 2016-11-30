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
    //1: ministop
    //2: family mart
    //3: bsmart
    //4: shop n go

    public Store() {
    }

    public Store(String title, String lat, String lng, String tel ,int type)
    {
        this.title = title;
        this.lat = lat;
        this.lng = lng;
        this.tel = tel;
        this.type = type;
    }


    public Store(StoreEntity entity)
    {
        title = entity.getTitle();
        address = entity.getAddress();
        lat = String.valueOf(entity.getLatitude());
        lng = String.valueOf(entity.getLongitude());
        tel = entity.getTelephone();
        type = entity.getType();
    }

    public Store(StoreViewModel viewModel)
    {
        title = viewModel.getStoreName();
        lat = String.valueOf(viewModel.getPosition().latitude);
        lng = String.valueOf(viewModel.getPosition().longitude);
        tel = "3835 3193";
        type = viewModel.getType();
        address = viewModel.getStoreAddress();
    }


    public LatLng getPosition()
    {
        return new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
    }


    public String getTitle() {
        return title;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public int getType() {
        return type;
    }

    @PropertyName("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @PropertyName("lat")
    public void setLat(String lat) {
        this.lat = lat;
    }

    @PropertyName("lng")
    public void setLng(String lng) {
        this.lng = lng;
    }

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

    @PropertyName("title")
    private String title;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @PropertyName("address")
    private String address;
    @PropertyName("lat")
    private String lat;
    @PropertyName("lng")
    private String lng;
    @PropertyName("tel")
    private String tel;
    @Exclude
    int type;
}
