package com.iceteaviet.fastfoodfinder.data.local.user.model

import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import io.realm.RealmList
import io.realm.RealmObject

/**
 * Created by Genius Doan on 2018.
 */
open class UserEntity : RealmObject() {
    var name: String = ""
    var email: String = ""
    private var uid: String = ""
    var photoUrl: String = ""
    var userStoreLists: RealmList<UserStoreListEntity> = RealmList()

    fun getUid(): String {
        return uid
    }

    fun map(user: User) {
        name = user.name
        email = user.email
        uid = user.getUid()
        photoUrl = user.photoUrl
        this.userStoreLists = RealmList()
        val userStoreLists = user.getUserStoreLists()
        for (i in userStoreLists.indices) {
            this.userStoreLists.add(UserStoreListEntity(userStoreLists.get(i)))
        }
    }
}
