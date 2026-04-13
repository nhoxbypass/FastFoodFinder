package com.iceteaviet.fastfoodfinder.data.auth

import com.google.firebase.auth.AuthCredential
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException

class FakeFirebaseClientAuth : ClientAuth {
    private var user: User?

    init {
        user = User()
    }

    override fun getCurrentUserUid(): String {
        val currUser = user
        return if (currUser != null) currUser.getUid() else ""
    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String): User {
        if (user != null)
            return user!!
        else
            throw NotFoundException()
    }

    override fun isSignedIn(): Boolean {
        return user != null
    }

    override fun signOut() {
        user = null
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): User {
        if (user != null)
            return user!!
        else
            throw NotFoundException()
    }

    override suspend fun signInWithCredential(authCredential: AuthCredential): User {
        if (user != null)
            return user!!
        else
            throw NotFoundException()
    }
}
