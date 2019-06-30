package com.iceteaviet.fastfoodfinder.data.local.db.user

import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList

/**
 * Created by tom on 7/15/18.
 */
interface UserDataSource {
    fun insertOrUpdate(name: String, email: String, photoUrl: String, uid: String, storeLists: List<UserStoreList>)

    fun insertOrUpdate(user: User)

    fun updateStoreListForUser(uid: String, storeLists: List<UserStoreList>)

    fun getUser(uid: String): User

    fun isUserExists(uid: String): Boolean
}
