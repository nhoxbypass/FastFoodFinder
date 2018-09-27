package com.iceteaviet.fastfoodfinder.data.local.user.model

import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList

import io.realm.RealmList
import io.realm.RealmObject

/**
 * Created by tom on 7/26/18.
 */
open class UserStoreListEntity : RealmObject {
    var id: Int = 0
    lateinit var listName: String
    var iconId: Int = 0
    lateinit var storeIdList: RealmList<Int>

    constructor() {
        // Realm required
    }

    constructor(userStoreList: UserStoreList) {
        map(userStoreList)
    }

    fun map(userStoreList: UserStoreList) {
        id = userStoreList.id
        listName = userStoreList.listName
        iconId = userStoreList.iconId
        storeIdList = RealmList()
        storeIdList.addAll(userStoreList.getStoreIdList()!!)
    }
}
