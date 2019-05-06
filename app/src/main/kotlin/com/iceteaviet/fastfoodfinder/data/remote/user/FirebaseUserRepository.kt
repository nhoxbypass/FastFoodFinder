package com.iceteaviet.fastfoodfinder.data.remote.user

import android.util.Pair
import androidx.annotation.NonNull
import com.google.firebase.database.*
import com.iceteaviet.fastfoodfinder.data.domain.user.UserDataSource
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreEvent
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.utils.e
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by tom on 7/15/18.
 */
class FirebaseUserRepository(private val databaseRef: DatabaseReference) : UserDataSource {

    var favouriteStoresListener: ChildEventListener? = null

    override fun insertOrUpdate(name: String, email: String, photoUrl: String, uid: String, storeLists: List<UserStoreList>) {
        val user = User(uid, name, email, photoUrl, storeLists.toMutableList())
        insertOrUpdate(user)
    }


    override fun insertOrUpdate(user: User) {
        databaseRef.child(CHILD_USERS)
                .child(user.getUid())
                .setValue(user)
    }

    override fun updateStoreListForUser(uid: String, storeLists: List<UserStoreList>) {
        databaseRef.child(CHILD_USERS)
                .child(uid)
                .child(CHILD_USERS_STORE_LIST)
                .setValue(storeLists)
    }

    override fun getUser(uid: String): Single<User> {
        return Single.create { emitter ->

            databaseRef.child(CHILD_USERS).child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val user = dataSnapshot.getValue(User::class.java)!!
                        emitter.onSuccess(user)
                    } else {
                        emitter.onError(NotFoundException())
                    }
                }

                override fun onCancelled(@NonNull databaseError: DatabaseError) {
                    emitter.onError(databaseError.toException())
                }
            })
        }
    }

    override fun isUserExists(uid: String): Single<Boolean> {
        return Single.create { emitter ->
            databaseRef.child(CHILD_USERS).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists() || !dataSnapshot.hasChild(uid)) {
                        // Not exists
                        emitter.onSuccess(false)
                    } else {
                        emitter.onSuccess(true)
                    }
                }

                override fun onCancelled(@NonNull databaseError: DatabaseError) {
                    e(TAG, "Error checking user exists")
                    emitter.onError(databaseError.toException())
                }
            })
        }
    }

    override fun subscribeFavouriteStoresOfUser(uid: String): Observable<Pair<Int, Int>> {
        return Observable.create { emitter ->
            favouriteStoresListener = databaseRef.child(CHILD_USERS)
                    .child(uid)
                    .child(CHILD_USERS_STORE_LIST)
                    .child(UserStoreList.ID_FAVOURITE.toString())
                    .child(CHILD_STORE_ID_LIST).addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(@NonNull dataSnapshot: DataSnapshot, s: String?) {
                            if (dataSnapshot.exists())
                                emitter.onNext(Pair<Int, Int>(dataSnapshot.getValue(Int::class.java), UserStoreEvent.ACTION_ADDED))
                        }

                        override fun onChildChanged(@NonNull dataSnapshot: DataSnapshot, s: String?) {
                            if (dataSnapshot.exists())
                                emitter.onNext(Pair<Int, Int>(dataSnapshot.getValue(Int::class.java), UserStoreEvent.ACTION_CHANGED))
                        }

                        override fun onChildRemoved(@NonNull dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists())
                                emitter.onNext(Pair<Int, Int>(dataSnapshot.getValue(Int::class.java), UserStoreEvent.ACTION_REMOVED))
                        }

                        override fun onChildMoved(@NonNull dataSnapshot: DataSnapshot, s: String?) {
                            if (dataSnapshot.exists())
                                emitter.onNext(Pair<Int, Int>(dataSnapshot.getValue(Int::class.java), UserStoreEvent.ACTION_MOVED))
                        }

                        override fun onCancelled(@NonNull databaseError: DatabaseError) {
                            emitter.onError(databaseError.toException())
                        }
                    })
        }
    }

    override fun unsubscribeFavouriteStoresOfUser(uid: String) {
        favouriteStoresListener?.let {
            databaseRef.child(CHILD_USERS)
                    .child(uid)
                    .child(CHILD_USERS_STORE_LIST)
                    .child(UserStoreList.ID_FAVOURITE.toString())
                    .child(CHILD_STORE_ID_LIST).removeEventListener(it)
        }
    }

    companion object {
        private val TAG = FirebaseUserRepository::class.java.simpleName
        private const val CHILD_USERS = "users"
        private const val CHILD_USERS_STORE_LIST = "userStoreLists"
        private const val CHILD_STORE_ID_LIST = "storeIdList"
    }
}
