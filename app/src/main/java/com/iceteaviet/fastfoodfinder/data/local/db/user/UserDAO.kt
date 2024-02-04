package com.iceteaviet.fastfoodfinder.data.local.db.user

import com.iceteaviet.fastfoodfinder.data.local.db.user.model.UserEntity
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.UserStoreListEntity
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import io.realm.Realm
import io.realm.RealmList

/**
 * Created by tom on 7/25/18.
 */
class UserDAO : UserDataSource {

    override fun insertOrUpdate(name: String, email: String, photoUrl: String, uid: String, storeLists: List<UserStoreList>) {
        insertOrUpdate(User(uid, name, email, photoUrl, storeLists))
    }

    override fun insertOrUpdate(user: User) {
        val realm = Realm.getDefaultInstance()

        realm.executeTransactionAsync {
            it.where(UserEntity::class.java)
                .findAll()
                .deleteAllFromRealm()

            val userEntity = it.createObject(UserEntity::class.java)
            userEntity.map(user)
        }

        realm.close()
    }

    override fun updateStoreListForUser(uid: String, storeLists: List<UserStoreList>) {
        val realm = Realm.getDefaultInstance()

        realm.executeTransactionAsync {
            val entity = it.where(UserEntity::class.java)
                .equalTo(PARAM_UID, uid)
                .findFirst()
            if (entity != null) {
                /*val userStoreListEntities = RealmList<UserStoreListEntity>()
                for (i in storeLists.indices) {
                    userStoreListEntities.add(UserStoreListEntity(storeLists[i]))
                }
                val managedList = it.copyToRealm(userStoreListEntities)
                entity.userStoreLists.addAll(managedList)*/

                entity.userStoreLists = RealmList()
                for (i in storeLists.indices) {
                    entity.userStoreLists.add(UserStoreListEntity(storeLists[i]))
                }
            }
        }

        realm.close()
    }

    override fun getUser(uid: String): User {
        val realm = Realm.getDefaultInstance()

        val entity = realm.where(UserEntity::class.java)
            .equalTo(PARAM_UID, uid)
            .findFirst()

        realm.close()

        if (entity != null)
            return User(entity)
        else
            throw NotFoundException()

    }

    override fun isUserExists(uid: String): Boolean {
        val realm = Realm.getDefaultInstance()

        val count = realm.where(UserEntity::class.java)
            .equalTo(PARAM_UID, uid)
            .count()

        realm.close()

        return count > 0
    }

    companion object {
        private const val PARAM_UID = "uid"
    }
}
