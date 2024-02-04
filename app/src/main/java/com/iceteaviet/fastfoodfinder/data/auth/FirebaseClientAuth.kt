package com.iceteaviet.fastfoodfinder.data.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
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
        val currUser = mAuth.currentUser
        return if (currUser != null) currUser.uid else ""
    }

    override fun signUpWithEmailAndPassword(email: String, password: String): Single<User> {
        return Single.create { emitter ->
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    it.user?.let { user ->
                        emitter.onSuccess(convertFirebaseUserToUser(user))
                    }
                }
                .addOnFailureListener { e -> emitter.onError(e) }
                .addOnCanceledListener { emitter.onError(Exception("Cancel")) }
        }
    }

    override fun isSignedIn(): Boolean {
        val currUser = mAuth.currentUser
        return currUser != null && !currUser.isAnonymous
    }

    override fun signOut() {
        mAuth.signOut()
    }

    override fun signInWithEmailAndPassword(email: String, password: String): Single<User> {
        return Single.create { emitter ->
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    authResult.user?.let {
                        emitter.onSuccess(convertFirebaseUserToUser(it))
                    }
                }
                .addOnFailureListener { e -> emitter.onError(e) }
                .addOnCanceledListener { emitter.onError(Exception("Cancel")) }
        }
    }

    override fun signInWithCredential(authCredential: AuthCredential): Single<User> {
        return Single.create { emitter ->
            mAuth.signInWithCredential(authCredential)
                .addOnSuccessListener { authResult ->
                    authResult.user?.let {
                        emitter.onSuccess(convertFirebaseUserToUser(it))
                    }
                }
                .addOnFailureListener { e -> emitter.onError(e) }
                .addOnCanceledListener { emitter.onError(Exception("Cancel")) }
        }
    }

    private fun convertFirebaseUserToUser(firebaseUser: FirebaseUser): User {
        var photoUrl = ""

        if (firebaseUser.photoUrl != null) {
            photoUrl = firebaseUser.photoUrl.toString()
        }

        var displayName = ""
        firebaseUser.displayName?.let {
            displayName = it
        }

        val email = firebaseUser.email
        if (email != null)
            return User(firebaseUser.uid, displayName, email, photoUrl, ArrayList())
        else
            throw IllegalArgumentException()
    }
}
