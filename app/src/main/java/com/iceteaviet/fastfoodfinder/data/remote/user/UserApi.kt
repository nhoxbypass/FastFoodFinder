package com.iceteaviet.fastfoodfinder.data.remote.user

import android.util.Pair
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import io.reactivex.Observable

/**
 * Created by tom on 7/15/18.
 */
interface UserApi {
    interface UserLoadCallback<T> {
        fun onSuccess(data: T)

        fun onError(exception: Exception)
    }

    fun insertOrUpdate(name: String, email: String, photoUrl: String, uid: String, storeLists: List<UserStoreList>)

    fun insertOrUpdate(user: User)

    fun updateStoreListForUser(uid: String, storeLists: List<UserStoreList>)

    fun getUser(uid: String, callback: UserLoadCallback<User>)

    fun isUserExists(uid: String, callback: UserLoadCallback<Boolean>)

    fun subscribeFavouriteStoresOfUser(uid: String): Observable<Pair<Int, Int>>  // Pair <StoreId, Event code>

    fun unsubscribeFavouriteStoresOfUser(uid: String)
}
