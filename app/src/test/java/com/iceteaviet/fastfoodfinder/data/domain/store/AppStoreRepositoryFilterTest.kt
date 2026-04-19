package com.iceteaviet.fastfoodfinder.data.domain.store

import com.google.common.truth.Truth.assertThat
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.StoreType
import org.junit.Test

class AppStoreRepositoryFilterTest {

    private val repository = AppStoreRepository(
        storeApiHelper = object : com.iceteaviet.fastfoodfinder.data.remote.store.StoreApiHelper {
            override suspend fun getAllStores(): List<Store> = emptyList()
            override suspend fun getComments(storeId: String): List<com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment> = emptyList()
            override fun insertOrUpdateComment(storeId: String, comment: com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment) {}
        },
        storeDao = object : com.iceteaviet.fastfoodfinder.data.local.db.store.StoreDao {
            override suspend fun getAllStores(): List<com.iceteaviet.fastfoodfinder.data.local.db.store.model.StoreEntity> = emptyList()
            override suspend fun getStoreInBounds(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double): List<com.iceteaviet.fastfoodfinder.data.local.db.store.model.StoreEntity> = emptyList()
            override suspend fun findStoresByCustomAddress(query: String): List<com.iceteaviet.fastfoodfinder.data.local.db.store.model.StoreEntity> = emptyList()
            override suspend fun findStoresByType(type: Int): List<com.iceteaviet.fastfoodfinder.data.local.db.store.model.StoreEntity> = emptyList()
            override suspend fun findStoreById(id: Int): com.iceteaviet.fastfoodfinder.data.local.db.store.model.StoreEntity? = null
            override suspend fun findStoresByIds(ids: List<Int>): List<com.iceteaviet.fastfoodfinder.data.local.db.store.model.StoreEntity> = emptyList()
            override suspend fun insertAll(stores: List<com.iceteaviet.fastfoodfinder.data.local.db.store.model.StoreEntity>) {}
            override suspend fun deleteAll() {}
        }
    )

    @Test
    fun filterInvalidDataTest_emptyData() {
        assertThat(repository.filterInvalidData(emptyList())).isEmpty()
    }

    @Test
    fun filterInvalidDataTest_invalidData() {
        val stores = listOf(
            Store(STORE_INVALID_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE),
            Store(STORE_ID, STORE_TITLE, STORE_INVALID_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE),
            Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_INVALID_LAT, STORE_LNG, STORE_TEL, STORE_TYPE),
            Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_INVALID_LNG, STORE_TEL, STORE_TYPE),
        )

        assertThat(repository.filterInvalidData(stores)).isEmpty()
    }

    @Test
    fun filterInvalidDataTest() {
        val stores = listOf(
            Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE),
            Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, "0.0", STORE_LNG, STORE_TEL, STORE_TYPE),
            Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, "0.0", STORE_TEL, STORE_TYPE),
            Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_INVALID_TEL, STORE_TYPE),
        )

        assertThat(repository.filterInvalidData(stores)).hasSize(4)
    }

    companion object {
        private const val STORE_ID = 123
        private const val STORE_TITLE = "store_title"
        private const val STORE_ADDRESS = "store_address"
        private const val STORE_LAT = "10.773996"
        private const val STORE_LNG = "106.6898035"
        private const val STORE_TEL = "012345678965"

        private const val STORE_TYPE = StoreType.TYPE_CIRCLE_K

        private const val STORE_INVALID_ID = -1
        private const val STORE_INVALID_TEL = ""
        private const val STORE_INVALID_ADDRESS = ""
        private const val STORE_INVALID_LAT = "-1"
        private const val STORE_INVALID_LNG = "-1"
    }
}
