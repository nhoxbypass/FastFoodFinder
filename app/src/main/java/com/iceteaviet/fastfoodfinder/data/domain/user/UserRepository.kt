package com.iceteaviet.fastfoodfinder.data.domain.user

import androidx.core.util.Pair

import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList

import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by tom on 7/15/18.
 *
 * Main entry point for accessing user data.
 */
interface UserRepository {
    fun insertOrUpdateUser(name: String, email: String, photoUrl: String, uid: String, storeLists: List<UserStoreList>)

    fun insertOrUpdateUser(user: User)

    fun updateStoreListForUser(uid: String, storeLists: List<UserStoreList>)

    fun getUser(uid: String): Single<User>

    fun isUserExists(uid: String): Single<Boolean>

    fun subscribeFavouriteStoresOfUser(uid: String): Observable<Pair<Int, Int>>  // Pair <StoreId, Event code>

    fun unsubscribeFavouriteStoresOfUser(uid: String)
}
