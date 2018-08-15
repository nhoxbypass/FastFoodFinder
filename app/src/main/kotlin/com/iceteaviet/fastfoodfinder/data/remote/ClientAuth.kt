package com.iceteaviet.fastfoodfinder.data.remote

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

import io.reactivex.Single

/**
 * Created by tom on 7/17/18.
 */
interface ClientAuth {
    fun getCurrentUserUid(): String

    fun isSignedIn(): Boolean

    fun signOut()

    fun signInWithEmailAndPassword(email: String, password: String): Single<Boolean>

    fun signInWithCredential(authCredential: AuthCredential): Single<FirebaseUser>
}
