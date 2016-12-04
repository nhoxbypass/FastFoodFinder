package com.example.mypc.fastfoodfinder.model.Routing;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nhoxb on 11/11/2016.
 */
public class Step implements Parcelable {
    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };
    @SerializedName("distance")
    JsonObject distance;
    @SerializedName("duration")
    JsonObject duration;
    @SerializedName("start_location")
    MapCoordination startMapCoordination;
    @SerializedName("end_location")
    MapCoordination endMapCoordination;
    @SerializedName("html_instructions")
    String instruction;
    @SerializedName("maneuver")
    String direction;
    @SerializedName("travel_mode")
    String travelMode;

    protected Step(Parcel in) {
        JsonParser parser = new JsonParser();
        distance = parser.parse(in.readString()).getAsJsonObject();
        duration = parser.parse(in.readString()).getAsJsonObject();
        startMapCoordination = in.readParcelable(MapCoordination.class.getClassLoader());
        endMapCoordination = in.readParcelable(MapCoordination.class.getClassLoader());
        instruction = in.readString();
        travelMode = in.readString();
        direction = in.readString();
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

    public MapCoordination getStartMapCoordination() {
        return startMapCoordination;
    }

    public String getInstruction() {
        return instruction;
    }

    public MapCoordination getEndMapCoordination() {
        return endMapCoordination;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(distance.toString());
        parcel.writeString(duration.toString());
        parcel.writeParcelable(startMapCoordination, i);
        parcel.writeParcelable(endMapCoordination, i);
        parcel.writeString(instruction);
        parcel.writeString(travelMode);
        parcel.writeString(direction);
    }
}
