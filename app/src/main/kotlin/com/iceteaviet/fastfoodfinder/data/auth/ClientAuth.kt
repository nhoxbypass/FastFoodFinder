package com.iceteaviet.fastfoodfinder.data.auth

import com.google.firebase.auth.AuthCredential
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import io.reactivex.Single

/**
 * Created by tom on 7/17/18.
 */
interface ClientAuth {
    fun getCurrentUserUid(): String

    fun signUpWithEmailAndPassword(email: String, password: String): Single<User>

    fun isSignedIn(): Boolean

    fun signOut()

    fun signInWithEmailAndPassword(email: String, password: String): Single<User>

    fun signInWithCredential(authCredential: AuthCredential): Single<User>
}
