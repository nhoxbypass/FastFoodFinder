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

    fun getStoreInBounds(lat: Double, lng: Double, radius: Double): Single<List<Store>>

    fun findStores(queryString: String): Single<List<Store>>

    fun findStoresByCustomAddress(customQuerySearch: List<String>): Single<List<Store>>

    fun findStoresBy(key: String, value: Int): Single<List<Store>>

    fun findStoresBy(key: String, values: List<Int>): Single<List<Store>>

    fun findStoresByType(type: Int): Single<List<Store>>

    fun findStoreById(id: Int): Single<Store>

    fun findStoresByIds(ids: List<Int>): Single<List<Store>>

    fun deleteAllStores()

    fun getComments(storeId: String): Single<List<Comment>>

    fun insertOrUpdateComment(storeId: String, comment: Comment)
}
