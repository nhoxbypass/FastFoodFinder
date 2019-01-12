package com.iceteaviet.fastfoodfinder.data.local.store

import com.iceteaviet.fastfoodfinder.data.domain.store.StoreDataSource
import com.iceteaviet.fastfoodfinder.data.local.store.model.StoreEntity
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.StoreType
import com.iceteaviet.fastfoodfinder.utils.exception.EmptyParamsException
import com.iceteaviet.fastfoodfinder.utils.normalizeDistrictQuery
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.realm.Case
import io.realm.Realm

/**
 * Created by Genius Doan on 11/20/2016.
 */
class LocalStoreRepository : StoreDataSource {

    private var cachedStores: MutableList<Store>

    init {
        cachedStores = mutableListOf<Store>()
    }


    override fun getAllStores(): Single<MutableList<Store>> {
        return Single.create(SingleOnSubscribe { emitter ->
            if (cachedStores.isNotEmpty()) {
                emitter.onSuccess(cachedStores)
                return@SingleOnSubscribe
            }

            val realm = Realm.getDefaultInstance()
            val results = realm
                    .where(StoreEntity::class.java)
                    .findAll()

            cachedStores = ArrayList()
            for (i in results.indices) {
                val storeEntity = results[i]
                if (storeEntity != null)
                    cachedStores.add(Store(storeEntity))
            }

            realm.close()
            emitter.onSuccess(ArrayList(cachedStores))
        })
    }


    override fun setStores(storeList: List<Store>) {
        if (!storeList.isEmpty()) {
            // Cache
            cachedStores = ArrayList(storeList)

            // Write to persistence
            val realm = Realm.getDefaultInstance()
            realm.executeTransactionAsync { realm ->
                realm.where(StoreEntity::class.java)
                        .findAll()
                        .deleteAllFromRealm()

                for (i in storeList.indices) {
                    val storeEntity = realm.createObject(StoreEntity::class.java)
                    storeEntity.map(storeList[i])
                }
            }

            realm.close()
        }
    }

    override fun getStoreInBounds(minLat: Double, minLng: Double, maxLat: Double, maxLng: Double): Single<MutableList<Store>> {
        return Single.create { emitter ->
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
            emitter.onSuccess(storeList)
        }
    }

    override fun findStores(queryString: String): Single<MutableList<Store>> {
        val trimmedQuery = queryString.toLowerCase().trim { it <= ' ' }
        return when {
            isCircleKQuery(trimmedQuery) -> findStoresByType(StoreType.TYPE_CIRCLE_K)
            isMinisStopQuery(trimmedQuery) -> findStoresByType(StoreType.TYPE_MINI_STOP)
            isFamilyMartQuery(trimmedQuery) -> findStoresByType(StoreType.TYPE_FAMILY_MART)
            isShopnGoQuery(trimmedQuery) -> findStoresByType(StoreType.TYPE_SHOP_N_GO)
            isBsMartQuery(trimmedQuery) -> findStoresByType(StoreType.TYPE_BSMART)
            else -> // Cant determine
                // Quite hard to implement
                findStoresByCustomAddress(normalizeDistrictQuery(queryString))
        }
    }

    override fun findStoresByCustomAddress(customQuerySearch: List<String>): Single<MutableList<Store>> {
        return Single.create { emitter ->
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

            emitter.onSuccess(storeList)
        }
    }

    override fun findStoresBy(key: String, value: Int): Single<MutableList<Store>> {
        return Single.create { emitter ->
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

            emitter.onSuccess(storeList)
        }
    }

    override fun findStoresBy(key: String, values: List<Int>): Single<MutableList<Store>> {
        if (values.isEmpty())
            return Single.error(EmptyParamsException())
        else
            return Single.create { emitter ->
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
        val realm = Realm.getDefaultInstance()
        realm.where(StoreEntity::class.java)
                .findAll()
                .deleteAllFromRealm()

        realm.close()
    }

    fun clearCache() {
        cachedStores.clear()
    }

    private fun isCircleKQuery(query: String): Boolean {
        return query == "circle k" || query == "circlek"
    }

    private fun isMinisStopQuery(query: String): Boolean {
        return query == "mini stop" || query == "ministop"
    }

    private fun isFamilyMartQuery(queryString: String): Boolean {
        return queryString == "family mart" || queryString == "familymart"
    }

    private fun isShopnGoQuery(queryString: String): Boolean {
        return queryString == "shop and go" || queryString == "shopandgo" || queryString == "shop n go"
    }

    private fun isBsMartQuery(queryString: String): Boolean {
        return queryString == "bsmart" || queryString == "b smart" || queryString == "bs mart" || queryString == "bmart" || queryString == "b'smart" || queryString == "b's mart"
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