package com.iceteaviet.fastfoodfinder.data.domain.user

import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun insertOrUpdateUser(name: String, email: String, photoUrl: String, uid: String, storeLists: List<UserStoreList>)

    suspend fun insertOrUpdateUser(user: User)

    suspend fun updateStoreListForUser(uid: String, storeLists: List<UserStoreList>)

    suspend fun getUser(uid: String): User

    suspend fun isUserExists(uid: String): Boolean

    fun subscribeFavouriteStoresOfUser(uid: String): Flow<Pair<Int, Int>>

    fun unsubscribeFavouriteStoresOfUser(uid: String)
}
