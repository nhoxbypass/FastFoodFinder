package com.iceteaviet.fastfoodfinder.model.Store;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

/**
 * Created by nhoxb on 11/10/2016.
 */
public class Store implements Parcelable {

    //Type
    //0: circle_k
    //1: logo_ministop_red
    //2: family mart
    //3: logo_bsmart_red
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

    public Store(int id, String title, String address, String lat, String lng, String tel, int type) {
        this.id= id;
        this.title = title;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.tel = tel;
        this.type = type;
    }

    public Store(StoreEntity entity) {
        id = entity.getId();
        title = entity.getTitle();
        address = entity.getAddress();
        lat = String.valueOf(entity.getLatitude());
        lng = String.valueOf(entity.getLongitude());
        tel = entity.getTelephone();
        type = entity.getType();
    }

    public Store(StoreViewModel viewModel) {
        id = viewModel.getId();
        title = viewModel.getStoreName();
        lat = String.valueOf(viewModel.getPosition().latitude);
        lng = String.valueOf(viewModel.getPosition().longitude);
        tel = "3835 3193";
        type = viewModel.getType();
        address = viewModel.getStoreAddress();
    }


    protected Store(Parcel in) {
        type = in.readInt();
        id = in.readInt();
        title = in.readString();
        address = in.readString();
        lat = in.readString();
        lng = in.readString();
        tel = in.readString();
    }

    public static final Creator<Store> CREATOR = new Creator<Store>() {
        @Override
        public Store createFromParcel(Parcel in) {
            return new Store(in);
        }

        @Override
        public Store[] newArray(int size) {
            return new Store[size];
        }
    };

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(type);
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(address);
        parcel.writeString(lat);
        parcel.writeString(lng);
        parcel.writeString(tel);
    }
}
