package com.iceteaviet.fastfoodfinder.data.remote.store

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.getStoreType
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseStoreApiHelper(private val databaseRef: DatabaseReference) : StoreApiHelper {

    override suspend fun getAllStores(): List<Store> = suspendCancellableCoroutine { cont ->
        databaseRef.child(CHILD_STORES_LOCATION).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                cont.resume(parseStoresDataFromFirebase(dataSnapshot))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                cont.resumeWithException(databaseError.toException())
            }
        })
    }

    override suspend fun getComments(storeId: String): List<Comment> = suspendCancellableCoroutine { cont ->
        databaseRef.child(CHILD_COMMENT_LIST).child(storeId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                cont.resumeWithException(databaseError.toException())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                cont.resume(parseCommentsDataFromFirebase(snapshot))
            }
        })
    }

    override fun insertOrUpdateComment(storeId: String, comment: Comment) {
        databaseRef.child(CHILD_COMMENT_LIST).child(storeId).push().setValue(comment)
    }

    private fun parseStoresDataFromFirebase(dataSnapshot: DataSnapshot): MutableList<Store> {
        val storeList = ArrayList<Store>()
        for (child in dataSnapshot.children) {
            for (storeLocation in child.child(CHILD_MARKERS_ADD).children) {
                val store = storeLocation.getValue(Store::class.java)
                if (store != null) {
                    store.type = getStoreType(child.key)
                    storeList.add(store)
                }
            }
        }

        return storeList
    }

    private fun parseCommentsDataFromFirebase(dataSnapshot: DataSnapshot): MutableList<Comment> {
        val commentList = ArrayList<Comment>()
        for (child in dataSnapshot.children) {
            val comment = child.getValue(Comment::class.java)
            if (comment != null) {
                comment.id = child.key ?: ""
                commentList.add(comment)
            }
        }
        return commentList
    }

    companion object {
        private val TAG = FirebaseStoreApiHelper::class.java.simpleName
        private const val CHILD_STORES_LOCATION = "stores_location"
        private const val CHILD_MARKERS_ADD = "markers_add"
        private const val CHILD_COMMENT_LIST = "comment_list"
    }
}
