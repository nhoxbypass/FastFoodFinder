package com.iceteaviet.fastfoodfinder.data.remote.user

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreEvent
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.utils.e
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseUserApiHelper(private val databaseRef: DatabaseReference) : UserApiHelper {

    var favouriteStoresListener: ChildEventListener? = null

    override fun insertOrUpdate(name: String, email: String, photoUrl: String, uid: String, storeLists: List<UserStoreList>) {
        val user = User(uid, name, email, photoUrl, storeLists)
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

    override suspend fun getUser(uid: String): User = suspendCancellableCoroutine { cont ->
        databaseRef.child(CHILD_USERS).child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null)
                        cont.resume(user)
                    else
                        cont.resumeWithException(NotFoundException())
                } else {
                    cont.resumeWithException(NotFoundException())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                cont.resumeWithException(databaseError.toException())
            }
        })
    }

    override suspend fun isUserExists(uid: String): Boolean = suspendCancellableCoroutine { cont ->
        databaseRef.child(CHILD_USERS).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                cont.resume(!dataSnapshot.exists() || dataSnapshot.hasChild(uid))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                e(TAG, "Error checking user exists")
                cont.resumeWithException(databaseError.toException())
            }
        })
    }

    override fun subscribeFavouriteStoresOfUser(uid: String): Flow<Pair<Int, Int>> = callbackFlow {
        favouriteStoresListener = databaseRef.child(CHILD_USERS)
            .child(uid)
            .child(CHILD_USERS_STORE_LIST)
            .child(UserStoreList.ID_FAVOURITE.toString())
            .child(CHILD_STORE_ID_LIST).addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    if (dataSnapshot.exists())
                        trySend(Pair(dataSnapshot.getValue(Int::class.java)!!, UserStoreEvent.ACTION_ADDED))
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                    if (dataSnapshot.exists())
                        trySend(Pair(dataSnapshot.getValue(Int::class.java)!!, UserStoreEvent.ACTION_CHANGED))
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists())
                        trySend(Pair(dataSnapshot.getValue(Int::class.java)!!, UserStoreEvent.ACTION_REMOVED))
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
                    if (dataSnapshot.exists())
                        trySend(Pair(dataSnapshot.getValue(Int::class.java)!!, UserStoreEvent.ACTION_MOVED))
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    close(databaseError.toException())
                }
            })
        awaitClose()
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
        private val TAG = FirebaseUserApiHelper::class.java.simpleName
        private const val CHILD_USERS = "users"
        private const val CHILD_USERS_STORE_LIST = "userStoreLists"
        private const val CHILD_STORE_ID_LIST = "storeIdList"
    }
}
