package com.iceteaviet.fastfoodfinder.data.remote.store

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by tom on 7/17/18.
 */
class FakeFirebaseStoreRepository : StoreApi {

    private var STORE_SERVICE_DATA: MutableList<Store> = ArrayList()
    private var STORE_COMMENT_SERVICE_DATA: MutableMap<String, MutableList<Comment>> = TreeMap()

    override fun getAllStores(callback: StoreApi.StoreLoadCallback<List<Store>>) {
        if (!STORE_SERVICE_DATA.isEmpty())
            callback.onSuccess(STORE_SERVICE_DATA)
        else
            callback.onError(NotFoundException())
    }

    override fun getComments(storeId: String, callback: StoreApi.StoreLoadCallback<List<Comment>>) {
        callback.onSuccess(STORE_COMMENT_SERVICE_DATA.get(storeId) ?: ArrayList())
    }

    override fun insertOrUpdateComment(storeId: String, comment: Comment) {
        var comments = STORE_COMMENT_SERVICE_DATA.get(storeId)
        if (comments == null) {
            comments = ArrayList()
        }
        comments.add(comment)
        STORE_COMMENT_SERVICE_DATA.put(storeId, comments)
    }
}
