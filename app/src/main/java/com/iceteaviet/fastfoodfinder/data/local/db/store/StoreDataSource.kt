package com.iceteaviet.fastfoodfinder.data.local.db.store

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store

/**
 * Created by tom on 7/15/18.
 */
interface StoreDataSource {

    fun getAllStores(): List<Store>

    fun setStores(storeList: List<Store>)

    fun getStoreInBounds(minLat: Double, minLng: Double, maxLat: Double, maxLng: Double): List<Store>

    fun findStores(queryString: String): List<Store>

    fun findStoresByCustomAddress(customQuerySearch: List<String>): List<Store>

    fun findStoresBy(key: String, value: Int): List<Store>

    fun findStoresBy(key: String, values: List<Int>): List<Store>

    fun findStoresByType(type: Int): List<Store>

    fun findStoreById(id: Int): Store?

    fun findStoresByIds(ids: List<Int>): List<Store>

    fun deleteAllStores()
}
