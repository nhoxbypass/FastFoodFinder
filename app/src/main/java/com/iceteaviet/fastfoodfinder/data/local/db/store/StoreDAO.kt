package com.iceteaviet.fastfoodfinder.data.local.db.store

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.iceteaviet.fastfoodfinder.data.local.db.store.model.StoreEntity

@Dao
interface StoreDao {
    @Query("SELECT * FROM stores")
    suspend fun getAllStores(): List<StoreEntity>

    @Query("SELECT * FROM stores WHERE latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLng AND :maxLng")
    suspend fun getStoreInBounds(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double): List<StoreEntity>

    @Query("SELECT * FROM stores WHERE title LIKE '%' || :query || '%' COLLATE NOCASE OR address LIKE '%' || :query || '%' COLLATE NOCASE")
    suspend fun findStoresByCustomAddress(query: String): List<StoreEntity>

    @Query("SELECT * FROM stores WHERE type = :type")
    suspend fun findStoresByType(type: Int): List<StoreEntity>

    @Query("SELECT * FROM stores WHERE id = :id")
    suspend fun findStoreById(id: Int): StoreEntity?

    @Query("SELECT * FROM stores WHERE id IN (:ids)")
    suspend fun findStoresByIds(ids: List<Int>): List<StoreEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stores: List<StoreEntity>)

    @Query("DELETE FROM stores")
    suspend fun deleteAll()
}
