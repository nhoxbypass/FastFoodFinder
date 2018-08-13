package com.iceteaviet.fastfoodfinder.data.remote.routing.model

import android.os.Parcel
import android.os.Parcelable

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

/**
 * Created by Genius Doan on 11/11/2016.
 */
class MapCoordination constructor(`in`: Parcel) : Parcelable {
    @SerializedName("lat")
    val latitude: Double
    @SerializedName("lng")
    val longitude: Double

    val location: LatLng
        get() = LatLng(latitude, longitude)

    init {
        latitude = `in`.readDouble()
        longitude = `in`.readDouble()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    companion object CREATOR : Parcelable.Creator<MapCoordination> {
        override fun createFromParcel(parcel: Parcel): MapCoordination {
            return MapCoordination(parcel)
        }

        override fun newArray(size: Int): Array<MapCoordination?> {
            return arrayOfNulls(size)
        }
    }
}
