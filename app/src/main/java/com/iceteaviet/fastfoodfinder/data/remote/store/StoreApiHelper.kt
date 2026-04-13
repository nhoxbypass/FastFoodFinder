package com.iceteaviet.fastfoodfinder.data.remote.store

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store

interface StoreApiHelper {
    suspend fun getAllStores(): List<Store>

    suspend fun getComments(storeId: String): List<Comment>

    fun insertOrUpdateComment(storeId: String, comment: Comment)
}
