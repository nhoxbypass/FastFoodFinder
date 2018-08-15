package com.iceteaviet.fastfoodfinder.data.remote.user.model

import com.iceteaviet.fastfoodfinder.data.local.user.model.UserEntity
import com.iceteaviet.fastfoodfinder.data.local.user.model.UserStoreListEntity
import com.iceteaviet.fastfoodfinder.utils.wtf
import java.util.*

/**
 * Created by Genius Doan on 11/24/2016.
 */
class User {
    var name: String? = null
    var email: String? = null
    var uid: String = ""
    var photoUrl: String? = null
    private var userStoreLists: MutableList<UserStoreList>? = null

    val favouriteStoreList: UserStoreList?
        get() {
            for (i in userStoreLists!!.indices) {
                if (userStoreLists!![i].id == UserStoreList.ID_FAVOURITE) {
                    return userStoreLists!![i]
                }
            }

            wtf(User::class.java.name, "Cannot find favourite list !!!")
            return null
        }

    constructor() {}

    constructor(name: String, email: String, photoUrl: String, uid: String, storeLists: MutableList<UserStoreList>) {
        this.name = name
        this.email = email
        this.uid = uid
        this.photoUrl = photoUrl
        this.userStoreLists = storeLists
    }

    constructor(entity: UserEntity) {
        this.name = entity.name
        this.email = entity.email
        this.uid = entity.uid
        this.photoUrl = entity.photoUrl
        this.userStoreLists = ArrayList()

        val userStoreListEntities: List<UserStoreListEntity> = entity.userStoreLists
        for (i in userStoreListEntities.indices) {
            this.userStoreLists!!.add(UserStoreList(userStoreListEntities[i]))
        }
    }

    fun getUserStoreLists(): List<UserStoreList>? {
        return userStoreLists
    }

    fun setUserStoreLists(userStoreLists: MutableList<UserStoreList>) {
        this.userStoreLists = userStoreLists
    }

    fun addStoreList(list: UserStoreList) {
        userStoreLists!!.add(list)
    }

    fun removeStoreList(position: Int) {
        userStoreLists!!.removeAt(position)
    }
}
