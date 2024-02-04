package com.iceteaviet.fastfoodfinder.data.local.db.user

import com.iceteaviet.fastfoodfinder.data.local.db.user.model.UserEntity
import com.iceteaviet.fastfoodfinder.data.local.db.user.model.UserStoreListEntity
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import io.reactivex.Single
import io.realm.Realm
import io.realm.RealmList

/**
 * Created by tom on 7/25/18.
 */
class AsyncUserDAO {

    fun insertOrUpdate(name: String, email: String, photoUrl: String, uid: String, storeLists: List<UserStoreList>) {
        insertOrUpdate(User(uid, name, email, photoUrl, storeLists))
    }

    fun insertOrUpdate(user: User) {
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

    fun updateStoreListForUser(uid: String, storeLists: List<UserStoreList>) {
        val realm = Realm.getDefaultInstance()

        realm.executeTransactionAsync {
            val entity = it.where(UserEntity::class.java)
                .equalTo(PARAM_UID, uid)
                .findFirst()
            if (entity != null) {
                val userStoreListEntities = RealmList<UserStoreListEntity>()
                for (i in storeLists.indices) {
                    userStoreListEntities.add(UserStoreListEntity(storeLists[i]))
                }
                entity.userStoreLists = userStoreListEntities
            }
        }

        realm.close()
    }

    fun getUser(uid: String): Single<User> {
        return Single.create { emitter ->

            val realm = Realm.getDefaultInstance()

            val entity = realm.where(UserEntity::class.java)
                .equalTo(PARAM_UID, uid)
                .findFirst()
            if (entity != null)
                emitter.onSuccess(User(entity))
            else
                emitter.onError(NotFoundException())

            realm.close()
        }
    }

    fun isUserExists(uid: String): Single<Boolean> {
        return Single.create { emitter ->
            val realm = Realm.getDefaultInstance()

            val count = realm.where(UserEntity::class.java)
                .equalTo(PARAM_UID, uid)
                .count()
            if (count > 0)
                emitter.onSuccess(true)
            else
                emitter.onSuccess(false)

            realm.close()
        }
    }

    companion object {
        private const val PARAM_UID = "uid"
    }
}
