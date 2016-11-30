package com.example.mypc.fastfoodfinder.model.Routing;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by nhoxb on 11/11/2016.
 */
public class Route implements Parcelable {
    protected Route(Parcel in) {
        legList = in.createTypedArrayList(Leg.CREATOR);
        summary = in.readString();
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    public List<Leg> getLegList() {
        return legList;
    }

    public String getSummary() {
        return summary;
    }

    @SerializedName("legs")
    List<Leg> legList;
    @SerializedName("summary")
    String summary;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(legList);
        parcel.writeString(summary);
    }
}
