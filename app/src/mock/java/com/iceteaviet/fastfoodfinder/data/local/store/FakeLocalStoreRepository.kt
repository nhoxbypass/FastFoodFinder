package com.iceteaviet.fastfoodfinder.data.local.store

import androidx.annotation.VisibleForTesting
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreDataSource
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.exception.EmptyParamsException
import com.iceteaviet.fastfoodfinder.utils.getStoreTypeFromQuery
import com.iceteaviet.fastfoodfinder.utils.standardizeDistrictQuery
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Genius Doan on 11/20/2016.
 */
class FakeLocalStoreRepository : StoreDataSource {

    private var STORE_SERVICE_DATA: MutableList<Store> = ArrayList()
    private var STORE_SERVICE_DATA_MAP: Map<String, MutableList<Store>> = TreeMap()

    @VisibleForTesting
    var cachedStores: MutableList<Store>

    init {
        cachedStores = ArrayList()
    }


    override fun getAllStores(): Single<List<Store>> {
        return Single.create(SingleOnSubscribe { emitter ->
            if (cachedStores.isNotEmpty()) {
                emitter.onSuccess(cachedStores)
                return@SingleOnSubscribe
            }

            cachedStores = STORE_SERVICE_DATA

            emitter.onSuccess(ArrayList(cachedStores))
        })
    }


    override fun setStores(storeList: List<Store>) {
        if (!storeList.isEmpty()) {
            // Cache
            cachedStores = ArrayList(storeList)

            STORE_SERVICE_DATA = ArrayList(storeList)
        }
    }

    override fun getStoreInBounds(minLat: Double, minLng: Double, maxLat: Double, maxLng: Double): Single<MutableList<Store>> {
        return Single.create { emitter ->
            val storeList = ArrayList<Store>()
            // Build the query looking at all users:
            val query = ""

            val res = STORE_SERVICE_DATA_MAP.get(query)
            storeList.addAll(res ?: ArrayList())

            emitter.onSuccess(storeList)
        }
    }

    override fun findStores(queryString: String): Single<MutableList<Store>> {
        val storeType = getStoreTypeFromQuery(queryString)
        if (storeType != -1) {
            return findStoresByType(storeType)
        } else {
            // Cant determine
            // Quite hard to implement
            return findStoresByCustomAddress(standardizeDistrictQuery(queryString))
        }
    }

    override fun findStoresByCustomAddress(customQuerySearch: List<String>): Single<MutableList<Store>> {
        return Single.create { emitter ->
            val storeList = ArrayList<Store>()

            if (!customQuerySearch.isEmpty()) {
                val query = customQuerySearch.toString()

                val res = STORE_SERVICE_DATA_MAP.get(query)
                storeList.addAll(res ?: ArrayList())
            }

            emitter.onSuccess(storeList)
        }
    }

    override fun findStoresBy(key: String, value: Int): Single<MutableList<Store>> {
        return Single.create { emitter ->
            val storeList = ArrayList<Store>()

            val query = key

            val res = STORE_SERVICE_DATA_MAP.get(query)
            storeList.addAll(res ?: ArrayList())

            emitter.onSuccess(storeList)
        }
    }

    override fun findStoresBy(key: String, values: List<Int>): Single<MutableList<Store>> {
        if (values.isEmpty())
            return Single.error(EmptyParamsException())
        else
            return Single.create { emitter ->
                val storeList = ArrayList<Store>()

                val query = key

                val res = STORE_SERVICE_DATA_MAP.get(query)
                storeList.addAll(res ?: ArrayList())

                emitter.onSuccess(storeList)
            }
    }

    override fun findStoresByType(type: Int): Single<MutableList<Store>> {
        return findStoresBy(PARAM_TYPE, type)
    }


    override fun findStoresById(id: Int): Single<MutableList<Store>> {
        return findStoresBy(PARAM_ID, id)
    }

    override fun findStoresByIds(ids: List<Int>): Single<MutableList<Store>> {
        return findStoresBy(PARAM_ID, ids)
    }

    override fun deleteAllStores() {
        cachedStores.clear()
        STORE_SERVICE_DATA.clear()
    }

    fun clearCache() {
        cachedStores.clear()
        STORE_SERVICE_DATA.clear()
    }

    override fun getComments(storeId: String): Single<MutableList<Comment>> {
        return Single.never()
    }

    override fun insertOrUpdateComment(storeId: String, comment: Comment) {
        // TODO: Support this
    }

    companion object {
        private const val PARAM_ID = "id"
        private const val PARAM_TYPE = "type"
        private const val PARAM_LAT = "latitude"
        private const val PARAM_LNG = "longitude"
        private const val PARAM_TITLE = "title"
        private const val PARAM_ADDRESS = "address"
    }
}