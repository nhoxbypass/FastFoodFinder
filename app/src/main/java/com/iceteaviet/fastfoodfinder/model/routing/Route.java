package com.iceteaviet.fastfoodfinder.model.routing;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Genius Doan on 11/11/2016.
 */
public class Route implements Parcelable {


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
    @SerializedName("legs")
    List<Leg> legList;
    @SerializedName("summary")
    String summary;
    @SerializedName("overview_polyline")
    JsonObject encodedPolyline;

    protected Route(Parcel in) {
        legList = in.createTypedArrayList(Leg.CREATOR);
        summary = in.readString();
        JsonParser parser = new JsonParser();
        encodedPolyline = parser.parse(in.readString()).getAsJsonObject();
    }

    public List<Leg> getLegList() {
        return legList;
    }

    public String getSummary() {
        return summary;
    }

    public JsonObject getEncodedPolyline() {
        return encodedPolyline;
    }

    public void setEncodedPolyline(JsonObject encodedPolyline) {
        this.encodedPolyline = encodedPolyline;
    }

    public String getEncodedPolylineString() {
        return encodedPolyline.getAsJsonPrimitive("points").getAsString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(legList);
        parcel.writeString(summary);
        parcel.writeString(encodedPolyline.toString());
    }
}
