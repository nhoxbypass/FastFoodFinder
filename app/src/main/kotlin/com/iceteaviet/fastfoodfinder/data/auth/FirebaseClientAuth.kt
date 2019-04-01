package com.iceteaviet.fastfoodfinder.data.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.utils.Constant
import com.iceteaviet.fastfoodfinder.utils.getDefaultUserStoreLists
import io.reactivex.Single

/**
 * Created by Genius Doan on 14/07/2017.
 */

class FirebaseClientAuth : ClientAuth {
    private val mAuth: FirebaseAuth

    init {
        mAuth = FirebaseAuth.getInstance()
    }

    override fun getCurrentUserUid(): String {
        return if (mAuth.currentUser != null) mAuth.currentUser!!.uid else ""
    }

    override fun signUpWithEmailAndPassword(email: String, password: String): Single<User> {
        return Single.create { emitter ->
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { emitter.onSuccess(convertFirebaseUserToUser(it.user)) }
                    .addOnFailureListener { e -> emitter.onError(e) }
                    .addOnCanceledListener { emitter.onError(Exception("Cancel")) }
        }
    }

    override fun isSignedIn(): Boolean {
        return mAuth.currentUser != null && !mAuth.currentUser!!.isAnonymous
    }

    override fun signOut() {
        mAuth.signOut()
    }

    override fun signInWithEmailAndPassword(email: String, password: String): Single<User> {
        return Single.create { emitter ->
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authResult -> emitter.onSuccess(convertFirebaseUserToUser(authResult.user)) }
                    .addOnFailureListener { e -> emitter.onError(e) }
                    .addOnCanceledListener { emitter.onError(Exception("Cancel")) }
        }
    }

    override fun signInWithCredential(authCredential: AuthCredential): Single<User> {
        return Single.create { emitter ->
            mAuth.signInWithCredential(authCredential)
                    .addOnSuccessListener { authResult -> emitter.onSuccess(convertFirebaseUserToUser(authResult.user)) }
                    .addOnFailureListener { e -> emitter.onError(e) }
                    .addOnCanceledListener { emitter.onError(Exception("Cancel")) }
        }
    }

    private fun convertFirebaseUserToUser(firebaseUser: FirebaseUser): User {
        var photoUrl = Constant.NO_AVATAR_PLACEHOLDER_URL

        if (firebaseUser.photoUrl != null) {
            photoUrl = firebaseUser.photoUrl!!.toString()
        }

        var displayName = ""
        if (firebaseUser.displayName != null)
            displayName = firebaseUser.displayName!!


        return User(displayName, firebaseUser.email!!, photoUrl, firebaseUser.uid, getDefaultUserStoreLists().toMutableList())
    }
}
