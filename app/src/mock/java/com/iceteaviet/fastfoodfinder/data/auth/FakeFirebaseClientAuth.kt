package com.iceteaviet.fastfoodfinder.data.auth

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.AuthCredential
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException

class FakeFirebaseClientAuth(context: Context) : ClientAuth {
    private var user: User? = null
    private val prefs: SharedPreferences = context.getSharedPreferences("fake_auth_prefs", Context.MODE_PRIVATE)

    init {
        val savedEmail = prefs.getString("mock_user_email", null)
        val savedName = prefs.getString("mock_user_name", "Mock User")
        if (savedEmail != null) {
            user = User("mock_uid", savedName ?: "Mock User", savedEmail, "", com.iceteaviet.fastfoodfinder.utils.getDefaultUserStoreLists())
        }
    }

    override fun getCurrentUserUid(): String {
        return user?.getUid() ?: ""
    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String): User {
        val newUser = User("mock_uid", "Mock User", email, "", com.iceteaviet.fastfoodfinder.utils.getDefaultUserStoreLists())
        setUserState(newUser, email)
        return newUser
    }

    override fun isSignedIn(): Boolean {
        return user != null
    }

    override fun signOut() {
        user = null
        prefs.edit().remove("mock_user_email").apply()
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): User {
        // Return empty name so LoginViewModel.ensureBasicUserData() can derive
        // a proper display name from the email (e.g. "john" from "john@gmail.com").
        val newUser = User("mock_uid", "", email, "", com.iceteaviet.fastfoodfinder.utils.getDefaultUserStoreLists())
        if (email != "anonymous@fastfoodfinder.com") {
            setUserState(newUser, email)
        }
        return newUser
    }

    override suspend fun signInWithCredential(authCredential: AuthCredential): User {
        val email = "mock.user@gmail.com"
        val newUser = User("mock_uid", "Mock User", email, "", com.iceteaviet.fastfoodfinder.utils.getDefaultUserStoreLists())
        setUserState(newUser, email)
        return newUser
    }
    
    private fun setUserState(newUser: User, email: String) {
        user = newUser
        prefs.edit()
            .putString("mock_user_email", email)
            .putString("mock_user_name", newUser.name)
            .apply()
    }
}
