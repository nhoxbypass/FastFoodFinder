package com.iceteaviet.fastfoodfinder.data.remote.store

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import java.util.*
import kotlin.collections.ArrayList

class FakeFirebaseStoreApiHelper : StoreApiHelper {

    private var STORE_SERVICE_DATA: MutableList<Store> = ArrayList()
    private var STORE_COMMENT_SERVICE_DATA: MutableMap<String, MutableList<Comment>> = TreeMap()

    override suspend fun getAllStores(): List<Store> {
        if (STORE_SERVICE_DATA.isEmpty()) {
            STORE_SERVICE_DATA = com.iceteaviet.fastfoodfinder.utils.getFakeStoreList().toMutableList()
        }
        return STORE_SERVICE_DATA
    }

    override suspend fun getComments(storeId: String): List<Comment> {
        return STORE_COMMENT_SERVICE_DATA.get(storeId) ?: ArrayList()
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
