package com.iceteaviet.fastfoodfinder.data.local.db.store

import com.iceteaviet.fastfoodfinder.data.local.db.store.model.StoreEntity
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.getStoreTypeFromQuery
import com.iceteaviet.fastfoodfinder.utils.standardizeDistrictQuery
import io.realm.Case
import io.realm.Realm

/**
 * Created by Genius Doan on 11/20/2016.
 */
class StoreDAO : StoreDataSource {

    override fun getAllStores(): List<Store> {
        val realm = Realm.getDefaultInstance()
        val results = realm
                .where(StoreEntity::class.java)
                .findAll()

        val stores = ArrayList<Store>()
        for (i in results.indices) {
            val storeEntity = results[i]
            if (storeEntity != null)
                stores.add(Store(storeEntity))
        }

        realm.close()
        return stores
    }


    override fun setStores(storeList: List<Store>) {
        if (!storeList.isEmpty()) {
            // Write to persistence
            val realm = Realm.getDefaultInstance()
            realm.executeTransactionAsync {
                it.where(StoreEntity::class.java)
                        .findAll()
                        .deleteAllFromRealm()

                for (i in storeList.indices) {
                    val storeEntity = it.createObject(StoreEntity::class.java)
                    storeEntity.map(storeList[i])
                }
            }

            realm.close()
        }
    }

    override fun getStoreInBounds(minLat: Double, minLng: Double, maxLat: Double, maxLng: Double): List<Store> {
        val realm = Realm.getDefaultInstance()
        val storeList = ArrayList<Store>()
        // Build the query looking at all users:
        val query = realm.where(StoreEntity::class.java)

        // Add query conditions:
        query.between(PARAM_LAT, minLat, maxLat)
        query.between(PARAM_LNG, minLng, maxLng)

        // Execute the query:
        val results = query.findAll()

        for (i in results.indices) {
            val storeEntity = results[i]
            if (storeEntity != null) {
                val store = Store(storeEntity)
                storeList.add(store)
            }
        }

        realm.close()
        return storeList
    }

    override fun findStores(queryString: String): List<Store> {
        val storeType = getStoreTypeFromQuery(queryString)
        if (storeType != -1) {
            return findStoresByType(storeType)
        } else {
            // Cant determine
            // Quite hard to implement
            return findStoresByCustomAddress(standardizeDistrictQuery(queryString))
        }
    }

    override fun findStoresByCustomAddress(customQuerySearch: List<String>): List<Store> {
        val realm = Realm.getDefaultInstance()
        val storeList = ArrayList<Store>()

        val query = realm.where(StoreEntity::class.java)

        if (!customQuerySearch.isEmpty()) {
            query.contains(PARAM_TITLE, customQuerySearch[0], Case.INSENSITIVE)
            query.or().contains(PARAM_ADDRESS, customQuerySearch[0], Case.INSENSITIVE)

            for (i in 1 until customQuerySearch.size) {
                query.or().contains(PARAM_TITLE, customQuerySearch[i], Case.INSENSITIVE)
                query.or().contains(PARAM_ADDRESS, customQuerySearch[i], Case.INSENSITIVE)
            }

            val results = query.findAll()

            val size = results.size
            for (i in 0 until size) {
                val storeEntity = results[i]
                if (storeEntity != null) {
                    val store = Store(storeEntity)
                    storeList.add(store)
                }
            }
        }

        realm.close()

        return storeList
    }

    override fun findStoresBy(key: String, value: Int): List<Store> {
        val realm = Realm.getDefaultInstance()
        val storeList = ArrayList<Store>()

        val query = realm.where(StoreEntity::class.java)

        query.equalTo(key, value)

        val results = query.findAll()

        val size = results.size
        for (i in 0 until size) {
            val storeEntity = results[i]
            if (storeEntity != null) {
                val store = Store(storeEntity)
                storeList.add(store)
            }
        }

        realm.close()

        return storeList
    }

    override fun findStoresBy(key: String, values: List<Int>): List<Store> {
        if (values.isEmpty())
            return ArrayList()
        else {
            val realm = Realm.getDefaultInstance()
            val storeList = ArrayList<Store>()

            val query = realm.where(StoreEntity::class.java)

            query.equalTo(key, values[0])
            for (i in 1 until values.size) {
                query.or().equalTo(key, values[i])
            }

            val results = query.findAll()

            val size = results.size
            for (i in 0 until size) {
                val storeEntity = results[i]
                if (storeEntity != null) {
                    val store = Store(storeEntity)
                    storeList.add(store)
                }
            }

            realm.close()
            return storeList
        }
    }

    override fun findStoresByType(type: Int): List<Store> {
        return findStoresBy(PARAM_TYPE, type)
    }


    override fun findStoreById(id: Int): Store? {
        val stores = findStoresBy(PARAM_ID, id)
        return if (stores.isEmpty())
            null
        else
            stores[0]
    }

    override fun findStoresByIds(ids: List<Int>): List<Store> {
        return findStoresBy(PARAM_ID, ids)
    }

    override fun deleteAllStores() {
        val realm = Realm.getDefaultInstance()
        realm.where(StoreEntity::class.java)
                .findAll()
                .deleteAllFromRealm()

        realm.close()
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