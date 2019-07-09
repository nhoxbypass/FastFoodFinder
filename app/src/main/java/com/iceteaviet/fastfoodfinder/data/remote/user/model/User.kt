package com.iceteaviet.fastfoodfinder.data.remote.user.model

import com.google.firebase.database.Exclude
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.UserEntity
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.UserStoreListEntity
import com.iceteaviet.fastfoodfinder.utils.realmListToList
import com.iceteaviet.fastfoodfinder.utils.wtf

/**
 * Created by Genius Doan on 11/24/2016.
 */
class User {
    var name: String = ""
    var email: String = ""
    private var uid: String = ""
    var photoUrl: String = ""
    private var userStoreLists: MutableList<UserStoreList> = ArrayList()

    constructor()

    constructor(user: User) : this(user.uid, user.name, user.email, user.photoUrl, user.userStoreLists)

    constructor(uid: String, name: String, email: String, photoUrl: String, storeLists: List<UserStoreList>) {
        this.uid = uid
        this.name = name
        this.email = email
        this.photoUrl = photoUrl
        this.userStoreLists = storeLists.toMutableList()
    }

    constructor(entity: UserEntity) {
        this.uid = entity.getUid()
        this.name = entity.name
        this.email = entity.email
        this.photoUrl = entity.photoUrl

        val userStoreListEntities: List<UserStoreListEntity> = realmListToList(entity.userStoreLists)
        this.userStoreLists = ArrayList()
        for (i in userStoreListEntities.indices) {
            this.userStoreLists.add(UserStoreList(userStoreListEntities[i]))
        }
    }

    fun getUid(): String {
        return uid
    }

    fun getUserStoreLists(): List<UserStoreList> {
        return userStoreLists
    }

    fun setUserStoreLists(userStoreLists: List<UserStoreList>) {
        this.userStoreLists = userStoreLists.toMutableList()
    }

    fun addStoreList(list: UserStoreList) {
        userStoreLists.add(list)
    }

    fun removeStoreList(position: Int) {
        userStoreLists.removeAt(position)
    }

    @Exclude
    fun getFavouriteStoreList(): UserStoreList {
        for (i in userStoreLists.indices) {
            if (userStoreLists[i].id == UserStoreList.ID_FAVOURITE) {
                return userStoreLists[i]
            }
        }

        wtf(User::class.java.name, "Cannot find favourite list !!!")
        return UserStoreList()
    }

    @Exclude
    fun getSavedStoreList(): UserStoreList {
        for (i in userStoreLists.indices) {
            if (userStoreLists[i].id == UserStoreList.ID_SAVED) {
                return userStoreLists[i]
            }
        }

        wtf(User::class.java.name, "Cannot find saved list !!!")
        return UserStoreList()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is User) {
            uid.equals(other.uid) && name.equals(other.name) && email.equals(other.email)
                    && photoUrl.equals(other.photoUrl) && userStoreLists.equals(other.userStoreLists)
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + uid.hashCode()
        result = 31 * result + photoUrl.hashCode()
        result = 31 * result + userStoreLists.hashCode()
        return result
    }
}
