package com.iceteaviet.fastfoodfinder.data.remote.routing.model

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.SerializedName

/**
 * Created by Genius Doan on 11/11/2016.
 */
class MapsDirection constructor(`in`: Parcel) : Parcelable {
    @SerializedName("routes")
    val routeList: List<Route>

    init {
        routeList = `in`.createTypedArrayList(Route.CREATOR) ?: ArrayList()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeTypedList(routeList)
    }

    companion object CREATOR : Parcelable.Creator<MapsDirection> {
        override fun createFromParcel(parcel: Parcel): MapsDirection {
            return MapsDirection(parcel)
        }

        override fun newArray(size: Int): Array<MapsDirection?> {
            return arrayOfNulls(size)
        }
    }
}
