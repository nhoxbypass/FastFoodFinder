package com.iceteaviet.fastfoodfinder.data.local.user

import android.util.Pair
import com.iceteaviet.fastfoodfinder.data.domain.user.UserDataSource
import com.iceteaviet.fastfoodfinder.data.local.user.model.UserEntity
import com.iceteaviet.fastfoodfinder.data.local.user.model.UserStoreListEntity
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import io.reactivex.Observable
import io.reactivex.Single
import io.realm.Realm
import io.realm.RealmList

/**
 * Created by tom on 7/25/18.
 */
class LocalUserRepository : UserDataSource {

    override fun insertOrUpdate(name: String, email: String, photoUrl: String, uid: String, storeLists: List<UserStoreList>) {
        insertOrUpdate(User(uid, name, email, photoUrl, storeLists))
    }

    override fun insertOrUpdate(user: User) {
        val realm = Realm.getDefaultInstance()

        realm.executeTransactionAsync { realm ->
            realm.where(UserEntity::class.java)
                    .findAll()
                    .deleteAllFromRealm()

            val userEntity = realm.createObject(UserEntity::class.java)
            userEntity.map(user)
        }

        realm.close()
    }

    override fun updateStoreListForUser(uid: String, storeLists: List<UserStoreList>) {
        val realm = Realm.getDefaultInstance()

        realm.executeTransactionAsync { realm ->
            val entity = realm.where(UserEntity::class.java)
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

    override fun getUser(uid: String): Single<User> {
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

    override fun isUserExists(uid: String): Single<Boolean> {
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

    @Deprecated("")
    override fun subscribeFavouriteStoresOfUser(uid: String): Observable<Pair<Int, Int>> {
        return Observable.create {
            val realm = Realm.getDefaultInstance()

            realm.where(UserEntity::class.java)
                    .equalTo(PARAM_UID, uid)
                    .findAll()
                    .addChangeListener { userEntities, changeSet -> }

            realm.close()
        }
    }

    @Deprecated("")
    override fun unsubscribeFavouriteStoresOfUser(uid: String) {
        val realm = Realm.getDefaultInstance()

        realm.where(UserEntity::class.java)
                .equalTo(PARAM_UID, uid)
                .findAll()
                .removeAllChangeListeners()

        realm.close()
    }

    companion object {
        private const val PARAM_UID = "uid"
    }
}
