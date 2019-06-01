package com.iceteaviet.fastfoodfinder.data.domain.store

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store

import io.reactivex.Single

/**
 * Created by tom on 7/15/18.
 *
 * Main entry point for accessing store data.
 */
interface StoreRepository {

    fun getAllStores(): Single<List<Store>>

    fun setStores(storeList: List<Store>)

    fun getStoreInBounds(minLat: Double, minLng: Double, maxLat: Double, maxLng: Double): Single<MutableList<Store>>

    fun findStores(queryString: String): Single<MutableList<Store>>

    fun findStoresByCustomAddress(customQuerySearch: List<String>): Single<MutableList<Store>>

    fun findStoresBy(key: String, value: Int): Single<MutableList<Store>>

    fun findStoresBy(key: String, values: List<Int>): Single<MutableList<Store>>

    fun findStoresByType(type: Int): Single<MutableList<Store>>

    fun findStoresById(id: Int): Single<MutableList<Store>>

    fun findStoresByIds(ids: List<Int>): Single<MutableList<Store>>

    fun deleteAllStores()

    fun getComments(storeId: String): Single<List<Comment>>

    fun insertOrUpdateComment(storeId: String, comment: Comment)
}
