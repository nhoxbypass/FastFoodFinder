package com.iceteaviet.fastfoodfinder.data.domain.store

import androidx.annotation.VisibleForTesting
import com.iceteaviet.fastfoodfinder.data.local.db.store.StoreDao
import com.iceteaviet.fastfoodfinder.data.local.db.store.model.toEntity
import com.iceteaviet.fastfoodfinder.data.local.db.store.model.toDomain
import com.iceteaviet.fastfoodfinder.data.remote.store.StoreApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.domain.model.Store
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import com.iceteaviet.fastfoodfinder.utils.getStoreTypeFromQuery
import com.iceteaviet.fastfoodfinder.utils.standardizeDistrictQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class AppStoreRepository(private val storeApiHelper: StoreApiHelper, private val storeDao: StoreDao) : StoreRepository {

    @VisibleForTesting
    internal var cachedStores: List<Store> = ArrayList()

    private val cacheMutex = Mutex()

    override suspend fun getAllStores(): List<Store> = withContext(Dispatchers.IO) {
        cacheMutex.withLock {
            if (cachedStores.isNotEmpty()) {
                return@withContext ArrayList(cachedStores)
            }

            val localStores = storeDao.getAllStores().map { it.toDomain() }
            if (localStores.isNotEmpty()) {
                cachedStores = localStores
                return@withContext ArrayList(localStores)
            }

            val remoteStores = storeApiHelper.getAllStores()
            val filtered = filterInvalidData(remoteStores)
            persistStoresLocked(filtered)
            ArrayList(filtered)
        }
    }

    override suspend fun refreshStores(): List<Store> = withContext(Dispatchers.IO) {
        cacheMutex.withLock {
            val remoteStores = storeApiHelper.getAllStores()
            val filtered = filterInvalidData(remoteStores)
            persistStoresLocked(filtered)
            ArrayList(filtered)
        }
    }

    override suspend fun setStores(storeList: List<Store>) = withContext(Dispatchers.IO) {
        cacheMutex.withLock {
            persistStoresLocked(storeList)
        }
    }

    private suspend fun persistStoresLocked(stores: List<Store>) {
        cachedStores = stores
        if (stores.isNotEmpty()) {
            storeDao.insertAll(stores.map { it.toEntity() })
        }
    }

    override suspend fun getStoreInBounds(lat: Double, lng: Double, radius: Double): List<Store> = withContext(Dispatchers.IO) {
        storeDao.getStoreInBounds(lat - radius, lat + radius, lng - radius, lng + radius).map { it.toDomain() }
    }

    override suspend fun findStores(queryString: String): List<Store> = withContext(Dispatchers.IO) {
        val storeType = getStoreTypeFromQuery(queryString)
        if (storeType != -1) {
            storeDao.findStoresByType(storeType).map { it.toDomain() }
        } else {
            val queries = standardizeDistrictQuery(queryString)
            queries.flatMap { storeDao.findStoresByCustomAddress(it).map { e -> e.toDomain() } }.distinctBy { it.id }
        }
    }

    override suspend fun findStoresByCustomAddress(customQuerySearch: List<String>): List<Store> = withContext(Dispatchers.IO) {
        customQuerySearch.flatMap { storeDao.findStoresByCustomAddress(it).map { e -> e.toDomain() } }.distinctBy { it.id }
    }

    override suspend fun findStoresByType(type: Int): List<Store> = withContext(Dispatchers.IO) {
        storeDao.findStoresByType(type).map { it.toDomain() }
    }

    override suspend fun findStoreById(id: Int): Store = withContext(Dispatchers.IO) {
        storeDao.findStoreById(id)?.toDomain() ?: throw NotFoundException()
    }

    override suspend fun findStoresByIds(ids: List<Int>): List<Store> = withContext(Dispatchers.IO) {
        storeDao.findStoresByIds(ids).map { it.toDomain() }
    }

    override suspend fun deleteAllStores() = withContext(Dispatchers.IO) {
        cacheMutex.withLock {
            cachedStores = ArrayList()
            storeDao.deleteAll()
        }
    }

    override suspend fun getComments(storeId: String): List<Comment> = withContext(Dispatchers.IO) {
        storeApiHelper.getComments(storeId)
    }

    override suspend fun insertOrUpdateComment(storeId: String, comment: Comment) {
        storeApiHelper.insertOrUpdateComment(storeId, comment)
    }

    fun clearCache() {
        cachedStores = ArrayList()
    }

    @VisibleForTesting
    internal fun filterInvalidData(stores: List<Store>): List<Store> {
        return stores.filter { store ->
            store.id >= 0 && store.lat in 0.0..90.0 && store.lng in 0.0..180.0 && store.address.isNotBlank()
        }
    }
}
