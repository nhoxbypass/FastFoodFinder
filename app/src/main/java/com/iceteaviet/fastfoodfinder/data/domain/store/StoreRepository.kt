package com.iceteaviet.fastfoodfinder.data.domain.store

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store

interface StoreRepository {

    suspend fun getAllStores(): List<Store>

    suspend fun refreshStores(): List<Store>

    suspend fun setStores(storeList: List<Store>)

    suspend fun getStoreInBounds(lat: Double, lng: Double, radius: Double): List<Store>

    suspend fun findStores(queryString: String): List<Store>

    suspend fun findStoresByCustomAddress(customQuerySearch: List<String>): List<Store>

    suspend fun findStoresByType(type: Int): List<Store>

    suspend fun findStoreById(id: Int): Store

    suspend fun findStoresByIds(ids: List<Int>): List<Store>

    suspend fun deleteAllStores()

    suspend fun getComments(storeId: String): List<Comment>

    suspend fun insertOrUpdateComment(storeId: String, comment: Comment)
}
