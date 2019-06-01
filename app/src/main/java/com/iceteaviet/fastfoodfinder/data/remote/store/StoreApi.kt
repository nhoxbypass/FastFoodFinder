package com.iceteaviet.fastfoodfinder.data.remote.store

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store

/**
 * Created by tom on 7/15/18.
 */
interface StoreApi {
    interface StoreLoadCallback<T> {
        fun onSuccess(data: T)

        fun onError(exception: Exception)
    }

    fun getAllStores(callback: StoreLoadCallback<List<Store>>)

    fun getComments(storeId: String, callback: StoreLoadCallback<List<Comment>>)

    fun insertOrUpdateComment(storeId: String, comment: Comment)
}
