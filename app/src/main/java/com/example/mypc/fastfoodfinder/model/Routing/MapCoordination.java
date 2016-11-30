package com.example.mypc.fastfoodfinder.model.Routing;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nhoxb on 11/11/2016.
 */
public class MapCoordination  implements Parcelable{
    protected MapCoordination(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<MapCoordination> CREATOR = new Creator<MapCoordination>() {
        @Override
        public MapCoordination createFromParcel(Parcel in) {
            return new MapCoordination(in);
        }

        @Override
        public MapCoordination[] newArray(int size) {
            return new MapCoordination[size];
        }
    };

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LatLng getLocation()
    {
        return new LatLng(latitude, longitude);
    }

    @SerializedName("lat")
    double latitude;
    @SerializedName("lng")
    double longitude;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }
}
