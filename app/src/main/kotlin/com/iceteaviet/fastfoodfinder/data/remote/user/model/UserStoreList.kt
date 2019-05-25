package com.iceteaviet.fastfoodfinder.data.remote.user.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.PropertyName
import com.iceteaviet.fastfoodfinder.data.local.user.model.UserStoreListEntity
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.realmListToList

/**
 * Created by Genius Doan on 12/6/2016.
 */
class UserStoreList : Parcelable {
    var id: Int = -1
    var listName: String = ""
    var iconId: Int = -1
    private var storeIdList: MutableList<Int> = ArrayList()

    protected constructor(`in`: Parcel) {
        id = `in`.readInt()
        listName = `in`.readString()
        iconId = `in`.readInt()
        storeIdList = ArrayList()
        `in`.readList(storeIdList, Int::class.java.classLoader)
    }

    constructor()


    constructor(id: Int, storeIdList: MutableList<Int>?, iconId: Int, listName: String) {
        this.id = id
        this.iconId = iconId
        this.listName = listName

        if (storeIdList == null)
            this.storeIdList = ArrayList()
        else
            this.storeIdList = storeIdList
    }

    constructor(userStoreListEntity: UserStoreListEntity) {
        this.id = userStoreListEntity.id
        this.iconId = userStoreListEntity.iconId
        this.listName = userStoreListEntity.listName
        this.storeIdList = realmListToList(userStoreListEntity.storeIdList)
    }

    fun getStoreIdList(): MutableList<Int> {
        return storeIdList
    }

    @PropertyName("storeIdList")
    fun setStoreIdList(storeIdList: MutableList<Int>) {
        this.storeIdList = storeIdList
    }

    fun addStore(store: Store) {
        storeIdList.add(store.id)
    }

    fun addStore(storeId: Int) {
        storeIdList.add(storeId)
    }

    fun removeStore(storeId: Int) {
        storeIdList.remove(Integer.valueOf(storeId))
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeInt(id)
        parcel.writeString(listName)
        parcel.writeInt(iconId)
        parcel.writeList(storeIdList)
    }

    companion object CREATOR : Parcelable.Creator<UserStoreList> {
        const val ID_SAVED = 0
        const val ID_FAVOURITE = 1

        override fun createFromParcel(parcel: Parcel): UserStoreList {
            return UserStoreList(parcel)
        }

        override fun newArray(size: Int): Array<UserStoreList?> {
            return arrayOfNulls(size)
        }
    }
}
