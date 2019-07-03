package com.iceteaviet.fastfoodfinder.data.remote.routing.model

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName

/**
 * Created by Genius Doan on 11/11/2016.
 */
class Step constructor(`in`: Parcel) : Parcelable {

    @SerializedName("distance")
    private val distance: JsonObject
    @SerializedName("duration")
    private val duration: JsonObject
    @SerializedName("start_location")
    val startMapCoordination: MapCoordination
    @SerializedName("end_location")
    val endMapCoordination: MapCoordination
    @SerializedName("html_instructions")
    val instruction: String
    @SerializedName("maneuver")
    var direction: String
    @SerializedName("travel_mode")
    val travelMode: String

    val distanceValue: Long
        get() = distance.getAsJsonPrimitive("value").asLong

    val durationValue: Long
        get() = duration.getAsJsonPrimitive("value").asLong

    init {
        val parser = JsonParser()
        distance = parser.parse(`in`.readString()).asJsonObject
        duration = parser.parse(`in`.readString()).asJsonObject
        startMapCoordination = `in`.readParcelable(MapCoordination::class.java.classLoader)
                ?: MapCoordination()
        endMapCoordination = `in`.readParcelable(MapCoordination::class.java.classLoader)
                ?: MapCoordination()
        instruction = `in`.readString() ?: ""
        travelMode = `in`.readString() ?: ""
        direction = `in`.readString() ?: ""
    }

    fun getDistance(): String {
        return distance.getAsJsonPrimitive("text").asString
    }

    fun getDuration(): String {
        return duration.getAsJsonPrimitive("text").asString
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(distance.toString())
        parcel.writeString(duration.toString())
        parcel.writeParcelable(startMapCoordination, i)
        parcel.writeParcelable(endMapCoordination, i)
        parcel.writeString(instruction)
        parcel.writeString(travelMode)
        parcel.writeString(direction)
    }

    companion object CREATOR : Parcelable.Creator<Step> {
        override fun createFromParcel(parcel: Parcel): Step {
            return Step(parcel)
        }

        override fun newArray(size: Int): Array<Step?> {
            return arrayOfNulls(size)
        }
    }
}
