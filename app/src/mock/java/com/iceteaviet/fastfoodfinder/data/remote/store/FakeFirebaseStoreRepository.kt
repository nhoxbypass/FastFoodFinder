package com.iceteaviet.fastfoodfinder.data.remote.store

import com.iceteaviet.fastfoodfinder.data.domain.store.StoreDataSource
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import com.iceteaviet.fastfoodfinder.utils.exception.NotSupportedException
import io.reactivex.Single
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by tom on 7/17/18.
 */
class FakeFirebaseStoreRepository : StoreDataSource {

    private var STORE_SERVICE_DATA: MutableList<Store> = ArrayList()
    private var STORE_COMMENT_SERVICE_DATA: MutableMap<String, MutableList<Comment>> = TreeMap()

    override fun setStores(storeList: List<Store>) {
        STORE_SERVICE_DATA = storeList.toMutableList()
    }

    override fun getAllStores(): Single<List<Store>> {
        return Single.create { emitter ->
            if (!STORE_SERVICE_DATA.isEmpty())
                emitter.onSuccess(STORE_SERVICE_DATA)
            else
                emitter.onError(NotFoundException())
        }
    }

    override fun getStoreInBounds(minLat: Double, minLng: Double, maxLat: Double, maxLng: Double): Single<MutableList<Store>> {
        return Single.error(NotSupportedException())
    }

    override fun findStores(queryString: String): Single<MutableList<Store>> {
        return Single.error(NotSupportedException())
    }

    override fun findStoresByCustomAddress(customQuerySearch: List<String>): Single<MutableList<Store>> {
        return Single.error(NotSupportedException())
    }

    override fun findStoresBy(key: String, value: Int): Single<MutableList<Store>> {
        return Single.error(NotSupportedException())
    }

    override fun findStoresBy(key: String, values: List<Int>): Single<MutableList<Store>> {
        return Single.error(NotSupportedException())
    }

    override fun findStoresByType(type: Int): Single<MutableList<Store>> {
        return Single.error(NotSupportedException())
    }

    override fun findStoresById(id: Int): Single<MutableList<Store>> {
        return Single.error(NotSupportedException())
    }

    override fun findStoresByIds(ids: List<Int>): Single<MutableList<Store>> {
        return Single.error(NotSupportedException())
    }

    override fun deleteAllStores() {
        STORE_SERVICE_DATA.clear()
    }

    override fun getComments(storeId: String): Single<MutableList<Comment>> {
        return Single.create { emitter ->
            emitter.onSuccess(STORE_COMMENT_SERVICE_DATA.get(storeId) ?: ArrayList())
        }
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
