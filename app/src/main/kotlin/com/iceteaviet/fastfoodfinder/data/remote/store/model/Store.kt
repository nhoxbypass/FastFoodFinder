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
    private lateinit var position: LatLng

    constructor()

    constructor(id: Int, title: String?, address: String?, lat: String, lng: String, tel: String?, type: Int) {
        try {
            this.id = id
            this.title = title
            this.address = address
            this.lat = lat
            this.lng = lng
            this.tel = tel
            this.type = type

            position = LatLng(java.lang.Double.valueOf(lat)!!, java.lang.Double.valueOf(lng)!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    constructor(entity: StoreEntity) : this(entity.id, entity.title, entity.address, entity.latitude.toString(), entity.longitude.toString(), entity.telephone, entity.type)

    protected constructor(`in`: Parcel) : this(`in`.readInt(), `in`.readString(), `in`.readString(), `in`.readString(), `in`.readString(), `in`.readString(), `in`.readInt())


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
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(address)
        parcel.writeString(lat)
        parcel.writeString(lng)
        parcel.writeString(tel)
        parcel.writeInt(type)
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + id
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (address?.hashCode() ?: 0)
        result = 31 * result + (lat?.hashCode() ?: 0)
        result = 31 * result + (lng?.hashCode() ?: 0)
        result = 31 * result + (tel?.hashCode() ?: 0)
        result = 31 * result + position.hashCode()
        return result
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
