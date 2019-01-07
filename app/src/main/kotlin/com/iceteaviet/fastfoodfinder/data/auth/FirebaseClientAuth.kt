package com.iceteaviet.fastfoodfinder.data.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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

    override fun isSignedIn(): Boolean {
        return mAuth.currentUser != null && !mAuth.currentUser!!.isAnonymous
    }

    override fun signOut() {
        mAuth.signOut()
    }

    override fun signInWithEmailAndPassword(email: String, password: String): Single<Boolean> {
        return Single.create { emitter ->
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { emitter.onSuccess(true) }
                    .addOnFailureListener { e -> emitter.onError(e) }
                    .addOnCanceledListener { emitter.onError(Exception("Cancel")) }
        }
    }

    override fun signInWithCredential(authCredential: AuthCredential): Single<FirebaseUser> {
        return Single.create { emitter ->
            mAuth.signInWithCredential(authCredential)
                    .addOnSuccessListener { authResult -> emitter.onSuccess(authResult.user) }
                    .addOnFailureListener { e -> emitter.onError(e) }
                    .addOnCanceledListener { emitter.onError(Exception("Cancel")) }
        }
    }
}
