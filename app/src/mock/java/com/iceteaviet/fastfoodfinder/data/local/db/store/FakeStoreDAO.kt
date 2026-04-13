package com.iceteaviet.fastfoodfinder.data.local.db.store

import com.iceteaviet.fastfoodfinder.data.local.db.store.model.StoreEntity
import com.iceteaviet.fastfoodfinder.data.local.db.store.model.toDomain
import com.iceteaviet.fastfoodfinder.data.local.db.store.model.toEntity
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.getStoreTypeFromQuery
import com.iceteaviet.fastfoodfinder.utils.standardizeDistrictQuery
import java.util.*
import kotlin.collections.ArrayList

class FakeStoreDAO : StoreDao {

    private var STORE_SERVICE_DATA: MutableList<StoreEntity> = ArrayList()

    override suspend fun getAllStores(): List<StoreEntity> {
        return STORE_SERVICE_DATA
    }

    override suspend fun getStoreInBounds(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double): List<StoreEntity> {
        return STORE_SERVICE_DATA.filter { it.latitude in minLat..maxLat && it.longitude in minLng..maxLng }
    }

    override suspend fun findStoresByCustomAddress(query: String): List<StoreEntity> {
        return STORE_SERVICE_DATA.filter { it.title.contains(query, true) || it.address.contains(query, true) }
    }

    override suspend fun findStoresByType(type: Int): List<StoreEntity> {
        return STORE_SERVICE_DATA.filter { it.type == type }
    }

    override suspend fun findStoreById(id: Int): StoreEntity? {
        return STORE_SERVICE_DATA.find { it.id == id }
    }

    override suspend fun findStoresByIds(ids: List<Int>): List<StoreEntity> {
        return STORE_SERVICE_DATA.filter { it.id in ids }
    }

    override suspend fun insertAll(stores: List<StoreEntity>) {
        if (stores.isNotEmpty()) {
            STORE_SERVICE_DATA = ArrayList(stores)
        }
    }

    override suspend fun deleteAll() {
        STORE_SERVICE_DATA.clear()
    }
}
