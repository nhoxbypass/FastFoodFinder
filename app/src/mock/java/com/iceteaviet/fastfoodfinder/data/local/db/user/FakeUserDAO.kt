package com.iceteaviet.fastfoodfinder.data.local.db.user

import com.iceteaviet.fastfoodfinder.data.local.db.user.model.StoreIdEntity
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.UserEntity
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.UserStoreListEntity
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.UserStoreListWithIds
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.toDomain
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.toEntities
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.toEntity
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import java.util.*

class FakeUserDAO : UserDao {

    private var USER_SERVICE_DATA_MAP: MutableMap<String, User> = TreeMap()

    override suspend fun getUser(uid: String): UserEntity? {
        return USER_SERVICE_DATA_MAP[uid]?.toEntity()
    }

    override suspend fun isUserExists(uid: String): Int {
        return if (USER_SERVICE_DATA_MAP.containsKey(uid)) 1 else 0
    }

    override suspend fun insertOrUpdate(user: UserEntity) {
        val storeLists = getStoreListsForUser(user.uid)
        val domainUser = user.toDomain(storeLists)
        USER_SERVICE_DATA_MAP[user.uid] = domainUser
    }

    override suspend fun getStoreListsForUser(uid: String): List<UserStoreListWithIds> {
        val user = USER_SERVICE_DATA_MAP[uid] ?: return emptyList()
        return user.getUserStoreLists().mapIndexed { index, list ->
            val (listEntity, storeIds) = list.toEntities(uid, index)
            UserStoreListWithIds(listEntity, storeIds)
        }
    }

    override suspend fun insertStoreLists(storeLists: List<UserStoreListEntity>) {
    }

    override suspend fun insertStoreIds(items: List<StoreIdEntity>) {
    }

    override suspend fun deleteStoreListsForUser(uid: String) {
    }

    override suspend fun deleteStoreIdsForUser(uid: String) {
    }

    fun insertOrUpdateDirect(user: User) {
        USER_SERVICE_DATA_MAP[user.getUid()] = user
    }
}
