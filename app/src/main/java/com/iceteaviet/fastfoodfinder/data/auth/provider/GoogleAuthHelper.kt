package com.iceteaviet.fastfoodfinder.data.auth.provider

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.iceteaviet.fastfoodfinder.ui.login.LoginActivity.Companion.RC_GOOGLE_SIGN_IN

/**
 * Created by tom on 2019-05-03.
 */
class GoogleAuthHelper(private var activity: Activity, private var apiKey: String) : AbsAuthHelper<AuthCredential>(), AuthHelper<AuthCredential> {

    private var googleSignInClient: GoogleSignInClient? = null

    init {
        setupAuthProvider()
    }

    override fun setupAuthProvider() {
        googleSignInClient = setupGoogleSignInClient()
    }

    override fun startRequestAuthCredential() {
        // Check for existing Google Sign In account, if the user is already signed in
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        if (account != null) {
            listener?.onSuccess(GoogleAuthProvider.getCredential(account.idToken, null), true)
        } else {
            val signInIntent = googleSignInClient?.signInIntent
            activity.startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
        if (task.isSuccessful) {
            // Google Sign In was successful
            val account = task.getResult(ApiException::class.java)
            if (account != null)
                listener?.onSuccess(GoogleAuthProvider.getCredential(account.idToken, null), false)
            else
                listener?.onFailed()
        } else {
            listener?.onFailed()
        }
    }

    private fun setupGoogleSignInClient(): GoogleSignInClient {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val client: GoogleSignInClient
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(apiKey)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        client = GoogleSignIn.getClient(activity, gso)
        return client
    }
}