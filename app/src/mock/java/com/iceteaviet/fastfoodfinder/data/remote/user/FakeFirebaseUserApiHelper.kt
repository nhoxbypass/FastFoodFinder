package com.iceteaviet.fastfoodfinder.data.remote.user

import androidx.core.util.Pair
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import io.reactivex.Observable
import java.util.*

/**
 * Created by tom on 7/15/18.
 */
class FakeFirebaseUserApiHelper : UserApiHelper {

    private var USER_SERVICE_DATA_MAP: MutableMap<String, User> = TreeMap()


    override fun insertOrUpdate(name: String, email: String, photoUrl: String, uid: String, storeLists: List<UserStoreList>) {
        val user = User(uid, name, email, photoUrl, storeLists)
        insertOrUpdate(user)
    }


    override fun insertOrUpdate(user: User) {
        USER_SERVICE_DATA_MAP.put(user.getUid(), user)
    }

    override fun updateStoreListForUser(uid: String, storeLists: List<UserStoreList>) {
        val user = USER_SERVICE_DATA_MAP.get(uid)
        if (user != null) {
            user.setUserStoreLists(storeLists)
            USER_SERVICE_DATA_MAP.put(uid, user)
        }
    }

    override fun getUser(uid: String, callback: UserApiHelper.UserLoadCallback<User>) {
        val entity = USER_SERVICE_DATA_MAP.get(uid)
        if (entity != null)
            callback.onSuccess(entity)
        else
            callback.onError(NotFoundException())
    }

    override fun isUserExists(uid: String, callback: UserApiHelper.UserLoadCallback<Boolean>) {
        if (USER_SERVICE_DATA_MAP.containsKey(uid))
            callback.onSuccess(true)
        else
            callback.onSuccess(false)
    }

    override fun subscribeFavouriteStoresOfUser(uid: String): Observable<Pair<Int, Int>> {
        return Observable.create { emitter ->
        }
    }

    override fun unsubscribeFavouriteStoresOfUser(uid: String) {
    }
}
