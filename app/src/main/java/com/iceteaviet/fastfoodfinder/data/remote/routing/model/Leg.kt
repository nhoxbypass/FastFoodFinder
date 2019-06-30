package com.iceteaviet.fastfoodfinder.data.remote.routing.model

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName

/**
 * Created by Genius Doan on 11/11/2016.
 */
class Leg constructor(`in`: Parcel) : Parcelable {
    @SerializedName("distance")
    private val distance: JsonObject
    @SerializedName("duration")
    private val duration: JsonObject
    @SerializedName("start_address")
    val startAddress: String
    @SerializedName("end_address")
    val endAddress: String
    @SerializedName("steps")
    val stepList: List<Step>

    val distanceValue: Long
        get() = distance.getAsJsonPrimitive("value").asLong

    val durationValue: Long
        get() = duration.getAsJsonPrimitive("value").asLong

    init {
        val parser = JsonParser()
        distance = parser.parse(`in`.readString()).asJsonObject
        duration = parser.parse(`in`.readString()).asJsonObject
        startAddress = `in`.readString() ?: ""
        endAddress = `in`.readString() ?: ""
        stepList = `in`.createTypedArrayList(Step.CREATOR) ?: ArrayList()
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
        parcel.writeString(startAddress)
        parcel.writeString(endAddress)
        parcel.writeTypedList(stepList)
    }

    companion object CREATOR : Parcelable.Creator<Leg> {
        override fun createFromParcel(parcel: Parcel): Leg {
            return Leg(parcel)
        }

        override fun newArray(size: Int): Array<Leg?> {
            return arrayOfNulls(size)
        }
    }
}
