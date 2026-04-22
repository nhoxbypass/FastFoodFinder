package com.iceteaviet.fastfoodfinder.data.remote.store.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName
import com.iceteaviet.fastfoodfinder.domain.model.Store

class StoreDto {
    @Exclude
    @get:Exclude
    @set:Exclude
    var type: Int = 0

    @PropertyName("id")
    var id: Int = 0

    @PropertyName("title")
    @set:PropertyName("title")
    var title: String = ""

    @PropertyName("address")
    @set:PropertyName("address")
    var address: String = ""

    @PropertyName("lat")
    @set:PropertyName("lat")
    var lat: String = ""

    @PropertyName("lng")
    @set:PropertyName("lng")
    var lng: String = ""

    @PropertyName("tel")
    @set:PropertyName("tel")
    var tel: String = ""

    constructor()

    constructor(id: Int, title: String, address: String, lat: String, lng: String, tel: String, type: Int) {
        this.id = id
        this.title = title
        this.address = address
        this.lat = lat
        this.lng = lng
        this.tel = tel
        this.type = type
    }

    fun toDomain(): Store {
        return Store(
            id = id,
            title = title,
            address = address,
            lat = lat.toDoubleOrNull() ?: 0.0,
            lng = lng.toDoubleOrNull() ?: 0.0,
            tel = tel,
            type = type,
        )
    }
}
