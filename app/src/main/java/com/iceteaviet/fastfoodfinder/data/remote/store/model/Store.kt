package com.iceteaviet.fastfoodfinder.data.remote.store.model

import android.os.Parcel
import android.os.Parcelable

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName
import com.iceteaviet.fastfoodfinder.data.local.store.model.StoreEntity

/**
 * Created by Genius Doan on 11/10/2016.
 */
class Store : Parcelable {
    @Exclude
    @get:Exclude
    @set:Exclude
    var type: Int = 0
    @PropertyName("id")
    var id: Int = 0
    @PropertyName("title")
    @set:PropertyName("title")
    var title: String? = null
    @PropertyName("address")
    var address: String? = null
    @PropertyName("lat")
    @set:PropertyName("lat")
    var lat: String? = null
    @PropertyName("lng")
    @set:PropertyName("lng")
    var lng: String? = null
    @PropertyName("tel")
    @set:PropertyName("tel")
    var tel: String? = null
    @Exclude
    private var position: LatLng

    constructor() {}

    constructor(id: Int, title: String, address: String, lat: String, lng: String, tel: String, type: Int) {
        this.id = id
        this.title = title
        this.address = address
        this.lat = lat
        this.lng = lng
        this.tel = tel
        this.type = type
    }

    constructor(entity: StoreEntity) {
        id = entity.id
        title = entity.title
        address = entity.address
        lat = entity.latitude.toString()
        lng = entity.longitude.toString()
        tel = entity.telephone
        type = entity.type
    }


    protected constructor(`in`: Parcel) {
        type = `in`.readInt()
        id = `in`.readInt()
        title = `in`.readString()
        address = `in`.readString()
        lat = `in`.readString()
        lng = `in`.readString()
        tel = `in`.readString()
    }

    init {
        position = LatLng(java.lang.Double.valueOf(lat)!!, java.lang.Double.valueOf(lng)!!)
    }

    @Exclude
    fun getPosition(): LatLng {
        return position
    }

    override fun equals(obj: Any?): Boolean {
        return if (obj is Store) {
            this.id == obj.id && this.title == obj.title
        } else false

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeInt(type)
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(address)
        parcel.writeString(lat)
        parcel.writeString(lng)
        parcel.writeString(tel)
    }

    companion object CREATOR : Parcelable.Creator<Store> {
        // Type
        // 0: circle_k
        // 1: ministop
        // 2: family mart
        // 3: bsmart
        // 4: shop n go

        override fun createFromParcel(parcel: Parcel): Store {
            return Store(parcel)
        }

        override fun newArray(size: Int): Array<Store?> {
            return arrayOfNulls(size)
        }
    }
}
