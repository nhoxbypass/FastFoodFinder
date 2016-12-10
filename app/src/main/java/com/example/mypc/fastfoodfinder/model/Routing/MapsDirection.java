package com.example.mypc.fastfoodfinder.model.routing;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by nhoxb on 11/11/2016.
 */
public class MapsDirection  implements Parcelable{
    protected MapsDirection(Parcel in) {
        routeList = in.createTypedArrayList(Route.CREATOR);
    }

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

    public List<Route> getRouteList() {
        return routeList;
    }

    @SerializedName("routes")
    List<Route> routeList;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(routeList);
    }
}
