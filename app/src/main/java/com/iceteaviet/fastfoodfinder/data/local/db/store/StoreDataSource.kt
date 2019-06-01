package com.iceteaviet.fastfoodfinder.data.local.db.store

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store

/**
 * Created by tom on 7/15/18.
 */
interface StoreDataSource {

    fun getAllStores(): List<Store>

    fun setStores(storeList: List<Store>)

    fun getStoreInBounds(minLat: Double, minLng: Double, maxLat: Double, maxLng: Double): MutableList<Store>

    fun findStores(queryString: String): MutableList<Store>

    fun findStoresByCustomAddress(customQuerySearch: List<String>): MutableList<Store>

    fun findStoresBy(key: String, value: Int): MutableList<Store>

    fun findStoresBy(key: String, values: List<Int>): MutableList<Store>

    fun findStoresByType(type: Int): MutableList<Store>

    fun findStoresById(id: Int): MutableList<Store>

    fun findStoresByIds(ids: List<Int>): MutableList<Store>

    fun deleteAllStores()
}
