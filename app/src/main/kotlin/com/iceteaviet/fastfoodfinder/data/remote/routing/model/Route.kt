package com.iceteaviet.fastfoodfinder.data.remote.routing.model

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName

/**
 * Created by Genius Doan on 11/11/2016.
 */
class Route constructor(`in`: Parcel) : Parcelable {

    @SerializedName("legs")
    val legList: List<Leg>
    @SerializedName("summary")
    val summary: String
    @SerializedName("overview_polyline")
    var encodedPolyline: JsonObject

    val encodedPolylineString: String
        get() = encodedPolyline.getAsJsonPrimitive("points").asString

    init {
        legList = `in`.createTypedArrayList(Leg.CREATOR)
        summary = `in`.readString()
        val parser = JsonParser()
        encodedPolyline = parser.parse(`in`.readString()).asJsonObject
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeTypedList(legList)
        parcel.writeString(summary)
        parcel.writeString(encodedPolyline.toString())
    }

    companion object CREATOR : Parcelable.Creator<Route> {
        override fun createFromParcel(parcel: Parcel): Route {
            return Route(parcel)
        }

        override fun newArray(size: Int): Array<Route?> {
            return arrayOfNulls(size)
        }
    }
}
