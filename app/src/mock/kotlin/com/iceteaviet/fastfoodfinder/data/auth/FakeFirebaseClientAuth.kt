package com.iceteaviet.fastfoodfinder.data.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import io.reactivex.Single

/**
 * Created by Genius Doan on 14/07/2017.
 */

class FakeFirebaseClientAuth : ClientAuth {
    private var user: User?

    init {
        user = User()
    }

    override fun getCurrentUserUid(): String {
        val currUser = user
        return if (currUser != null) currUser.getUid() else ""
    }

    // TODO: Support fake user map to check user exist
    override fun signUpWithEmailAndPassword(email: String, password: String): Single<User> {
        return Single.create { emitter ->
            if (user != null)
                emitter.onSuccess(user!!)
            else
                emitter.onError(NotFoundException())
        }
    }

    override fun isSignedIn(): Boolean {
        return user != null
    }

    override fun signOut() {
        user = null
    }

    override fun signInWithEmailAndPassword(email: String, password: String): Single<User> {
        return Single.create { emitter ->
            if (user != null)
                emitter.onSuccess(user!!)
            else
                emitter.onError(NotFoundException())
        }
    }

    override fun signInWithCredential(authCredential: AuthCredential): Single<User> {
        return Single.create { emitter ->
            if (user != null)
                emitter.onSuccess(user!!)
            else
                emitter.onError(NotFoundException())
        }
    }
}
