package com.iceteaviet.fastfoodfinder.data.remote.routing.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Genius Doan on 11/11/2016.
 */
public class MapsDirection implements Parcelable {
    public static final Creator<MapsDirection> CREATOR = new Creator<MapsDirection>() {
        @Override
        public MapsDirection createFromParcel(Parcel in) {
            return new MapsDirection(in);
        }

        @Override
        public MapsDirection[] newArray(int size) {
            return new MapsDirection[size];
        }
    };
    @SerializedName("routes")
    List<Route> routeList;

    protected MapsDirection(Parcel in) {
        routeList = in.createTypedArrayList(Route.CREATOR);
    }

    public List<Route> getRouteList() {
        return routeList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(routeList);
    }
}
