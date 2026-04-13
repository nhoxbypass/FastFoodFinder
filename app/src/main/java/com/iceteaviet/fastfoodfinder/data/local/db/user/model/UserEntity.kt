package com.iceteaviet.fastfoodfinder.data.local.db.user.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
)

fun UserEntity.toDomain(storeLists: List<UserStoreListWithIds>): User {
    return User(uid, name, email, photoUrl, storeLists.map { it.toDomain() })
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        uid = getUid(),
        name = name,
        email = email,
        photoUrl = photoUrl,
    )
}
