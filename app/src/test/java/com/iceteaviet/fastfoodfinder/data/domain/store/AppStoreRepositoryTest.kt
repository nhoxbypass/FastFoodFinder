package com.iceteaviet.fastfoodfinder.data.domain.store

import com.iceteaviet.fastfoodfinder.data.local.db.store.StoreDao
import com.iceteaviet.fastfoodfinder.data.local.db.store.model.StoreEntity
import com.iceteaviet.fastfoodfinder.data.remote.store.StoreApiHelper
import com.iceteaviet.fastfoodfinder.domain.model.Store
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AppStoreRepositoryTest {

    private val storeApiHelper: StoreApiHelper = mock()
    private val storeDao: StoreDao = mock()

    private val repository = AppStoreRepository(storeApiHelper, storeDao)

    @Test
    fun getAllStores_returnsCachedStores_whenCacheIsNotEmpty() = runTest {
        val cachedStore = createStore(1)
        repository.cachedStores = listOf(cachedStore)

        val result = repository.getAllStores()

        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(cachedStore)
    }

    @Test
    fun getAllStores_returnsDatabaseStores_whenCacheIsEmptyAndDbHasData() = runTest {
        val entity = createStoreEntity(1)
        whenever(storeDao.getAllStores()).thenReturn(listOf(entity))

        val result = repository.getAllStores()

        assertThat(result).hasSize(1)
        assertThat(result[0].id).isEqualTo(1)
    }

    @Test
    fun getAllStores_fallsBackToRemote_whenCacheAndDbAreEmpty() = runTest {
        val remoteStore = createStore(1)
        whenever(storeDao.getAllStores()).thenReturn(emptyList())
        whenever(storeApiHelper.getAllStores()).thenReturn(listOf(remoteStore))

        val result = repository.getAllStores()

        assertThat(result).hasSize(1)
    }

    @Test
    fun getAllStores_filtersInvalidData_fromRemote() = runTest {
        val validStore = createStore(1)
        val invalidStore = Store(id = -1, title = "bad", address = "addr", lat = 10.0, lng = 106.0)
        whenever(storeDao.getAllStores()).thenReturn(emptyList())
        whenever(storeApiHelper.getAllStores()).thenReturn(listOf(validStore, invalidStore))

        val result = repository.getAllStores()

        assertThat(result).hasSize(1)
        assertThat(result[0].id).isEqualTo(1)
    }

    @Test
    fun refreshStores_alwaysFetchesFromRemote() = runTest {
        val remoteStore = createStore(1)
        repository.cachedStores = listOf(createStore(99))
        whenever(storeApiHelper.getAllStores()).thenReturn(listOf(remoteStore))

        val result = repository.refreshStores()

        assertThat(result).hasSize(1)
        assertThat(result[0].id).isEqualTo(1)
    }

    @Test
    fun refreshStores_persistsToDatabase() = runTest {
        val remoteStore = createStore(1)
        whenever(storeApiHelper.getAllStores()).thenReturn(listOf(remoteStore))

        repository.refreshStores()

        verify(storeDao).insertAll(org.mockito.kotlin.any())
    }

    @Test
    fun setStores_withNonEmptyList_insertsAll() = runTest {
        val stores = listOf(createStore(1), createStore(2))

        repository.setStores(stores)

        verify(storeDao).insertAll(org.mockito.kotlin.any())
    }

    @Test
    fun setStores_withEmptyList_deletesAll() = runTest {
        repository.setStores(emptyList())

        verify(storeDao).deleteAll()
    }

    @Test
    fun deleteAllStores_clearsCacheAndDeletesFromDb() = runTest {
        repository.cachedStores = listOf(createStore(1))

        repository.deleteAllStores()

        assertThat(repository.cachedStores).isEmpty()
        verify(storeDao).deleteAll()
    }

    @Test
    fun clearCache_onlyClearsCacheNotDb() = runTest {
        repository.cachedStores = listOf(createStore(1))

        repository.clearCache()

        assertThat(repository.cachedStores).isEmpty()
    }

    @Test
    fun findStoreById_returnsStore_whenFound() = runTest {
        val entity = createStoreEntity(42)
        whenever(storeDao.findStoreById(42)).thenReturn(entity)

        val result = repository.findStoreById(42)

        assertThat(result.id).isEqualTo(42)
    }

    @Test(expected = Exception::class)
    fun findStoreById_throws_whenNotFound() = runTest {
        whenever(storeDao.findStoreById(999)).thenReturn(null)

        repository.findStoreById(999)
    }

    @Test
    fun findStoresByType_delegatesToDao() = runTest {
        val entity = createStoreEntity(1)
        whenever(storeDao.findStoresByType(2)).thenReturn(listOf(entity))

        val result = repository.findStoresByType(2)

        assertThat(result).hasSize(1)
    }

    @Test
    fun findStoresByIds_delegatesToDao() = runTest {
        val entity = createStoreEntity(1)
        whenever(storeDao.findStoresByIds(listOf(1, 2))).thenReturn(listOf(entity))

        val result = repository.findStoresByIds(listOf(1, 2))

        assertThat(result).hasSize(1)
    }

    @Test
    fun getAllStores_returnsDefensiveCopy() = runTest {
        repository.cachedStores = listOf(createStore(1))

        val result = repository.getAllStores()
        repository.cachedStores = listOf(createStore(2))

        assertThat(result).hasSize(1)
        assertThat(result[0].id).isEqualTo(1)
    }

    companion object {
        private fun createStore(id: Int) = Store(
            id = id, title = "Store $id", address = "Address $id",
            lat = 10.0 + id * 0.01, lng = 106.0 + id * 0.01,
            tel = "090000000$id", type = 1
        )

        private fun createStoreEntity(id: Int) = StoreEntity(
            id = id, type = 1, title = "Store $id", address = "Address $id",
            latitude = 10.0 + id * 0.01, longitude = 106.0 + id * 0.01,
            telephone = "090000000$id"
        )
    }
}
