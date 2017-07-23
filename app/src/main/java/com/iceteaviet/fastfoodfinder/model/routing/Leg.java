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
public class Leg implements Parcelable {

    public static final Creator<Leg> CREATOR = new Creator<Leg>() {
        @Override
        public Leg createFromParcel(Parcel in) {
            return new Leg(in);
        }

        @Override
        public Leg[] newArray(int size) {
            return new Leg[size];
        }
    };
    @SerializedName("distance")
    JsonObject distance;
    @SerializedName("duration")
    JsonObject duration;
    @SerializedName("start_address")
    String startAddress;
    @SerializedName("end_address")
    String endAddress;
    @SerializedName("steps")
    List<Step> stepList;

    protected Leg(Parcel in) {
        JsonParser parser = new JsonParser();
        distance = parser.parse(in.readString()).getAsJsonObject();
        duration = parser.parse(in.readString()).getAsJsonObject();
        startAddress = in.readString();
        endAddress = in.readString();
        stepList = in.createTypedArrayList(Step.CREATOR);
    }

    public String getDistance() {
        return distance.getAsJsonPrimitive("text").getAsString();
    }

    public long getDistanceValue() {
        return distance.getAsJsonPrimitive("value").getAsLong();
    }

    public String getDuration() {
        return duration.getAsJsonPrimitive("text").getAsString();
    }

    public long getDurationValue() {
        return duration.getAsJsonPrimitive("value").getAsLong();
    }

    public String getStartAddress() {
        return startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public List<Step> getStepList() {
        return stepList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(distance.toString());
        parcel.writeString(duration.toString());
        parcel.writeString(startAddress);
        parcel.writeString(endAddress);
        parcel.writeTypedList(stepList);
    }
}
