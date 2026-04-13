package com.iceteaviet.fastfoodfinder.data.local.db.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.StoreIdEntity
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.UserEntity
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.UserStoreListEntity
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.UserStoreListWithIds

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUser(uid: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users WHERE uid = :uid")
    suspend fun isUserExists(uid: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserEntity)

    @Transaction
    @Query("SELECT * FROM user_store_lists WHERE userUid = :uid")
    suspend fun getStoreListsForUser(uid: String): List<UserStoreListWithIds>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStoreLists(storeLists: List<UserStoreListEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStoreIds(items: List<StoreIdEntity>)

    @Query("DELETE FROM user_store_lists WHERE userUid = :uid")
    suspend fun deleteStoreListsForUser(uid: String)

    @Query("DELETE FROM user_store_list_items WHERE listRowId IN (SELECT rowId FROM user_store_lists WHERE userUid = :uid)")
    suspend fun deleteStoreIdsForUser(uid: String)
}
