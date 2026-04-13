package com.iceteaviet.fastfoodfinder.data.domain.user

import com.iceteaviet.fastfoodfinder.data.local.db.user.UserDao
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.StoreIdEntity
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.UserEntity
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.UserStoreListEntity
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.toEntity
import com.iceteaviet.fastfoodfinder.data.remote.user.UserApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.toEntities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AppUserRepository(private val userApiHelper: UserApiHelper, private val userDao: UserDao) : UserRepository {

    override suspend fun insertOrUpdateUser(name: String, email: String, photoUrl: String, uid: String, storeLists: List<UserStoreList>) {
        insertOrUpdateUser(User(uid, name, email, photoUrl, storeLists))
    }

    override suspend fun insertOrUpdateUser(user: User) = withContext(Dispatchers.IO) {
        userApiHelper.insertOrUpdate(user)
        userDao.insertOrUpdate(user.toEntity())
        persistStoreLists(user.getUid(), user.getUserStoreLists())
    }

    override suspend fun updateStoreListForUser(uid: String, storeLists: List<UserStoreList>) = withContext(Dispatchers.IO) {
        userApiHelper.updateStoreListForUser(uid, storeLists)
        persistStoreLists(uid, storeLists)
    }

    override suspend fun getUser(uid: String): User = withContext(Dispatchers.IO) {
        userApiHelper.getUser(uid)
    }

    override suspend fun isUserExists(uid: String): Boolean = withContext(Dispatchers.IO) {
        userApiHelper.isUserExists(uid)
    }

    override fun subscribeFavouriteStoresOfUser(uid: String): Flow<Pair<Int, Int>> {
        return userApiHelper.subscribeFavouriteStoresOfUser(uid)
    }

    override fun unsubscribeFavouriteStoresOfUser(uid: String) {
        return userApiHelper.unsubscribeFavouriteStoresOfUser(uid)
    }

    private suspend fun persistStoreLists(uid: String, storeLists: List<UserStoreList>) {
        userDao.deleteStoreIdsForUser(uid)
        userDao.deleteStoreListsForUser(uid)
        val listEntities = mutableListOf<UserStoreListEntity>()
        val allStoreIds = mutableListOf<StoreIdEntity>()
        for ((index, list) in storeLists.withIndex()) {
            val (listEntity, storeIds) = list.toEntities(uid, index)
            listEntities.add(listEntity)
            allStoreIds.addAll(storeIds)
        }
        userDao.insertStoreLists(listEntities)
        userDao.insertStoreIds(allStoreIds)
    }
}
