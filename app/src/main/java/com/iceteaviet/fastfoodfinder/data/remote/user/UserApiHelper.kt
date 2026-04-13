package com.iceteaviet.fastfoodfinder.data.remote.user

import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import kotlinx.coroutines.flow.Flow

interface UserApiHelper {
    fun insertOrUpdate(name: String, email: String, photoUrl: String, uid: String, storeLists: List<UserStoreList>)

    fun insertOrUpdate(user: User)

    fun updateStoreListForUser(uid: String, storeLists: List<UserStoreList>)

    suspend fun getUser(uid: String): User

    suspend fun isUserExists(uid: String): Boolean

    fun subscribeFavouriteStoresOfUser(uid: String): Flow<Pair<Int, Int>>

    fun unsubscribeFavouriteStoresOfUser(uid: String)
}
