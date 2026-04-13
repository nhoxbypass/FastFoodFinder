package com.iceteaviet.fastfoodfinder.data.local.db.user.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList

@Entity(tableName = "user_store_lists", foreignKeys = [
    ForeignKey(entity = UserEntity::class, parentColumns = ["uid"], childColumns = ["userUid"], onDelete = ForeignKey.CASCADE)
])
data class UserStoreListEntity(
    @PrimaryKey(autoGenerate = true) val rowId: Int = 0,
    val userUid: String = "",
    val id: Int = 0,
    val listName: String = "",
    val iconId: Int = 0,
)

@Entity(tableName = "user_store_list_items", foreignKeys = [
    ForeignKey(entity = UserStoreListEntity::class, parentColumns = ["rowId"], childColumns = ["listRowId"], onDelete = ForeignKey.CASCADE)
])
data class StoreIdEntity(
    @PrimaryKey(autoGenerate = true) val rowId: Int = 0,
    val listRowId: Int = 0,
    val storeId: Int = 0,
)

data class UserStoreListWithIds(
    @Embedded val list: UserStoreListEntity,
    @Relation(parentColumn = "rowId", entityColumn = "listRowId")
    val storeIds: List<StoreIdEntity>,
) {
    fun toDomain(): UserStoreList {
        return UserStoreList(list.id, storeIds.map { it.storeId }, list.iconId, list.listName)
    }
}

fun UserStoreList.toEntities(userUid: String, listRowId: Int): Pair<UserStoreListEntity, List<StoreIdEntity>> {
    val entity = UserStoreListEntity(
        userUid = userUid,
        id = id,
        listName = listName,
        iconId = iconId,
    )
    val items = getStoreIdList().map { StoreIdEntity(listRowId = listRowId, storeId = it) }
    return entity to items
}
