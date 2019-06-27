package com.iceteaviet.fastfoodfinder.data.local.db.store.model

import com.google.android.gms.maps.model.LatLng
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store

import io.realm.RealmObject

/**
 * Created by Genius Doan on 11/20/2016.
 */
open class StoreEntity : RealmObject() {

    var type: Int = 0
        private set
    var id: Int = 0
    var title: String = ""
        private set
    var address: String = ""
    var latitude: Double = 0.toDouble()
        private set
    var longitude: Double = 0.toDouble()
        private set
    var telephone: String = ""

    val position: LatLng
        get() = LatLng(latitude, longitude)

    fun map(store: Store) {
        id = store.id
        title = store.title
        address = store.address
        latitude = java.lang.Double.parseDouble(store.lat)
        longitude = java.lang.Double.parseDouble(store.lng)
        telephone = store.tel
        type = store.type
    }
}
