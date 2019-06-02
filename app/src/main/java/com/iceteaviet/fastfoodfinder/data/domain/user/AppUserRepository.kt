package com.iceteaviet.fastfoodfinder.data.domain.user

import android.util.Pair
import com.iceteaviet.fastfoodfinder.data.local.db.user.UserDataSource
import com.iceteaviet.fastfoodfinder.data.remote.user.UserApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by tom on 2019-06-01.
 */
class AppUserRepository(private val userApiHelper: UserApiHelper, private val userDataSource: UserDataSource) : UserRepository {

    override fun insertOrUpdateUser(name: String, email: String, photoUrl: String, uid: String, storeLists: List<UserStoreList>) {
        userApiHelper.insertOrUpdate(User(uid, name, email, photoUrl, storeLists))
        userDataSource.insertOrUpdate(User(uid, name, email, photoUrl, storeLists))
    }

    override fun insertOrUpdateUser(user: User) {
        userApiHelper.insertOrUpdate(user)
        userDataSource.insertOrUpdate(user)
    }

    override fun updateStoreListForUser(uid: String, storeLists: List<UserStoreList>) {
        userDataSource.updateStoreListForUser(uid, storeLists)
    }

    override fun getUser(uid: String): Single<User> {
        return Single.create { emitter ->
            userApiHelper.getUser(uid, object : UserApiHelper.UserLoadCallback<User> {
                override fun onSuccess(data: User) {
                    emitter.onSuccess(data)
                }

                override fun onError(exception: Exception) {
                    emitter.onError(exception)
                }

            })
        }
    }

    override fun isUserExists(uid: String): Single<Boolean> {
        return Single.create { emitter ->
            userApiHelper.isUserExists(uid, object : UserApiHelper.UserLoadCallback<Boolean> {
                override fun onSuccess(data: Boolean) {
                    emitter.onSuccess(data)
                }

                override fun onError(exception: Exception) {
                    emitter.onError(exception)
                }

            })
        }
    }

    override fun subscribeFavouriteStoresOfUser(uid: String): Observable<Pair<Int, Int>> {
        return userApiHelper.subscribeFavouriteStoresOfUser(uid)
    }

    override fun unsubscribeFavouriteStoresOfUser(uid: String) {
        return userApiHelper.unsubscribeFavouriteStoresOfUser(uid)
    }
}