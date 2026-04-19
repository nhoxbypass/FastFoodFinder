package com.iceteaviet.fastfoodfinder.data.remote.store

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.StoreDto
import com.iceteaviet.fastfoodfinder.domain.model.Store
import com.iceteaviet.fastfoodfinder.utils.getStoreType
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseStoreApiHelper(private val databaseRef: DatabaseReference) : StoreApiHelper {

    override suspend fun getAllStores(): List<Store> = suspendCancellableCoroutine { cont ->
        val ref = databaseRef.child(CHILD_STORES_LOCATION)
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                cont.resume(parseStoresDataFromFirebase(dataSnapshot))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                cont.resumeWithException(databaseError.toException())
            }
        }
        ref.addListenerForSingleValueEvent(listener)
        cont.invokeOnCancellation { ref.removeEventListener(listener) }
    }

    override suspend fun getComments(storeId: String): List<Comment> = suspendCancellableCoroutine { cont ->
        val ref = databaseRef.child(CHILD_COMMENT_LIST).child(storeId)
        val listener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                cont.resumeWithException(databaseError.toException())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                cont.resume(parseCommentsDataFromFirebase(snapshot))
            }
        }
        ref.addListenerForSingleValueEvent(listener)
        cont.invokeOnCancellation { ref.removeEventListener(listener) }
    }

    override fun insertOrUpdateComment(storeId: String, comment: Comment) {
        databaseRef.child(CHILD_COMMENT_LIST).child(storeId).push().setValue(comment)
    }

    private fun parseStoresDataFromFirebase(dataSnapshot: DataSnapshot): List<Store> {
        val storeList = ArrayList<Store>()
        for (child in dataSnapshot.children) {
            for (storeLocation in child.child(CHILD_MARKERS_ADD).children) {
                val dto = storeLocation.getValue(StoreDto::class.java)
                if (dto != null) {
                    dto.type = getStoreType(child.key)
                    storeList.add(dto.toDomain())
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
