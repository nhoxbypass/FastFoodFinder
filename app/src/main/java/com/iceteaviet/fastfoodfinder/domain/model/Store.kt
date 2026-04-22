package com.iceteaviet.fastfoodfinder.domain.model

import android.os.Parcel
import android.os.Parcelable

data class Store(
    val id: Int = 0,
    val title: String = "",
    val address: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val tel: String = "",
    val type: Int = 0,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readInt(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(address)
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
        parcel.writeString(tel)
        parcel.writeInt(type)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Store> {
        override fun createFromParcel(parcel: Parcel): Store = Store(parcel)
        override fun newArray(size: Int): Array<Store?> = arrayOfNulls(size)
    }
}
