package com.iceteaviet.fastfoodfinder.data.auth

import com.google.firebase.auth.AuthCredential
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User

interface ClientAuth {
    fun getCurrentUserUid(): String

    suspend fun signUpWithEmailAndPassword(email: String, password: String): User

    fun isSignedIn(): Boolean

    fun signOut()

    suspend fun signInWithEmailAndPassword(email: String, password: String): User

    suspend fun signInWithCredential(authCredential: AuthCredential): User
}
