package com.iceteaviet.fastfoodfinder.data.local.user

import android.util.Pair
import com.iceteaviet.fastfoodfinder.data.domain.user.UserDataSource
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

/**
 * Created by tom on 7/25/18.
 */
class FakeLocalUserRepository : UserDataSource {

    private var USER_SERVICE_DATA_MAP: MutableMap<String, User> = TreeMap()

    override fun insertOrUpdate(name: String, email: String, photoUrl: String, uid: String, storeLists: List<UserStoreList>) {
        insertOrUpdate(User(uid, name, email, photoUrl, storeLists))
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

    override fun getUser(uid: String): Single<User> {
        return Single.create { emitter ->

            val entity = USER_SERVICE_DATA_MAP.get(uid)
            if (entity != null)
                emitter.onSuccess(entity)
            else
                emitter.onError(NotFoundException())
        }
    }

    override fun isUserExists(uid: String): Single<Boolean> {
        return Single.create { emitter ->
            if (USER_SERVICE_DATA_MAP.containsKey(uid))
                emitter.onSuccess(true)
            else
                emitter.onSuccess(false)
        }
    }

    @Deprecated("")
    override fun subscribeFavouriteStoresOfUser(uid: String): Observable<Pair<Int, Int>> {
        return Observable.create {
        }
    }

    @Deprecated("")
    override fun unsubscribeFavouriteStoresOfUser(uid: String) {
    }
}
