package com.iceteaviet.fastfoodfinder.data.local.db.store.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iceteaviet.fastfoodfinder.domain.model.Store

@Entity(tableName = "stores")
data class StoreEntity(
    @PrimaryKey val id: Int = 0,
    val type: Int = 0,
    val title: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val telephone: String = "",
)

fun StoreEntity.toDomain(): Store {
    return Store(id, title, address, latitude, longitude, telephone, type)
}

fun Store.toEntity(): StoreEntity {
    return StoreEntity(
        id = id,
        type = type,
        title = title,
        address = address,
        latitude = lat,
        longitude = lng,
        telephone = tel,
    )
}
