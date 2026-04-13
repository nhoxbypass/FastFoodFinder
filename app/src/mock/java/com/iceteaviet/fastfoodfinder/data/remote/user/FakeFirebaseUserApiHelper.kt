package com.iceteaviet.fastfoodfinder.data.remote.user

import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import java.util.*

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

    override suspend fun getUser(uid: String): User {
        val entity = USER_SERVICE_DATA_MAP.get(uid)
        if (entity != null)
            return entity
        else
            throw NotFoundException()
    }

    override suspend fun isUserExists(uid: String): Boolean {
        return USER_SERVICE_DATA_MAP.containsKey(uid)
    }

    override fun subscribeFavouriteStoresOfUser(uid: String): Flow<Pair<Int, Int>> {
        return emptyFlow()
    }

    override fun unsubscribeFavouriteStoresOfUser(uid: String) {
    }
}
