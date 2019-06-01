package com.iceteaviet.fastfoodfinder.data.local.db.store

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.getStoreTypeFromQuery
import com.iceteaviet.fastfoodfinder.utils.standardizeDistrictQuery
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Genius Doan on 11/20/2016.
 */
class FakeLocalStoreRepository : StoreDataSource {

    private var STORE_SERVICE_DATA: MutableList<Store> = ArrayList()
    private var STORE_SERVICE_DATA_MAP: Map<String, MutableList<Store>> = TreeMap()


    override fun getAllStores(): List<Store> {
        return STORE_SERVICE_DATA
    }


    override fun setStores(storeList: List<Store>) {
        if (!storeList.isEmpty()) {
            STORE_SERVICE_DATA = ArrayList(storeList)
        }
    }

    override fun getStoreInBounds(minLat: Double, minLng: Double, maxLat: Double, maxLng: Double): MutableList<Store> {
        val storeList = ArrayList<Store>()
        // Build the query looking at all users:
        val query = ""

        val res = STORE_SERVICE_DATA_MAP.get(query)
        storeList.addAll(res ?: ArrayList())

        return storeList
    }

    override fun findStores(queryString: String): MutableList<Store> {
        val storeType = getStoreTypeFromQuery(queryString)
        if (storeType != -1) {
            return findStoresByType(storeType)
        } else {
            // Cant determine
            // Quite hard to implement
            return findStoresByCustomAddress(standardizeDistrictQuery(queryString))
        }
    }

    override fun findStoresByCustomAddress(customQuerySearch: List<String>): MutableList<Store> {
        val storeList = ArrayList<Store>()

        if (!customQuerySearch.isEmpty()) {
            val query = customQuerySearch.toString()

            val res = STORE_SERVICE_DATA_MAP.get(query)
            storeList.addAll(res ?: ArrayList())
        }

        return storeList
    }

    override fun findStoresBy(key: String, value: Int): MutableList<Store> {
        val storeList = ArrayList<Store>()

        val query = key

        val res = STORE_SERVICE_DATA_MAP.get(query)
        storeList.addAll(res ?: ArrayList())

        return storeList
    }

    override fun findStoresBy(key: String, values: List<Int>): MutableList<Store> {
        if (values.isEmpty())
            return ArrayList()
        else {
            val storeList = ArrayList<Store>()

            val query = key

            val res = STORE_SERVICE_DATA_MAP.get(query)
            storeList.addAll(res ?: ArrayList())

            return storeList
        }
    }

    override fun findStoresByType(type: Int): MutableList<Store> {
        return findStoresBy(PARAM_TYPE, type)
    }


    override fun findStoresById(id: Int): MutableList<Store> {
        return findStoresBy(PARAM_ID, id)
    }

    override fun findStoresByIds(ids: List<Int>): MutableList<Store> {
        return findStoresBy(PARAM_ID, ids)
    }

    override fun deleteAllStores() {
        STORE_SERVICE_DATA.clear()
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