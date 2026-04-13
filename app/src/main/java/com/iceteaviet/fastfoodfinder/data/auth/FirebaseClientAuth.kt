package com.iceteaviet.fastfoodfinder.data.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import kotlinx.coroutines.tasks.await

class FirebaseClientAuth : ClientAuth {
    private val mAuth: FirebaseAuth

    init {
        mAuth = FirebaseAuth.getInstance()
    }

    override fun getCurrentUserUid(): String {
        val currUser = mAuth.currentUser
        return if (currUser != null) currUser.uid else ""
    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String): User {
        val result = mAuth.createUserWithEmailAndPassword(email, password).await()
        return convertFirebaseUserToUser(result.user!!)
    }

    override fun isSignedIn(): Boolean {
        val currUser = mAuth.currentUser
        return currUser != null && !currUser.isAnonymous
    }

    override fun signOut() {
        mAuth.signOut()
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): User {
        val result = mAuth.signInWithEmailAndPassword(email, password).await()
        return convertFirebaseUserToUser(result.user!!)
    }

    override suspend fun signInWithCredential(authCredential: AuthCredential): User {
        val result = mAuth.signInWithCredential(authCredential).await()
        return convertFirebaseUserToUser(result.user!!)
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
