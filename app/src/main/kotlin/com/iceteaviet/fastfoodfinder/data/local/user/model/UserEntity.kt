package com.iceteaviet.fastfoodfinder.data.local.user.model

import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import io.realm.RealmList
import io.realm.RealmObject

/**
 * Created by Genius Doan on 2018.
 */
open class UserEntity : RealmObject() {

    lateinit var name: String
    lateinit var email: String
    lateinit var uid: String
    lateinit var photoUrl: String
    lateinit var userStoreLists: RealmList<UserStoreListEntity>

    fun map(user: User) {
        name = user.name
        email = user.email
        uid = user.uid
        photoUrl = user.photoUrl
        this.userStoreLists = RealmList()
        val userStoreLists = user.getUserStoreLists()
        if (userStoreLists != null) {
            for (i in userStoreLists.indices) {
                this.userStoreLists.add(UserStoreListEntity(userStoreLists.get(i)))
            }
        }
    }
}
