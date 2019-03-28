package com.iceteaviet.fastfoodfinder.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.ui.main.MainActivity
import com.iceteaviet.fastfoodfinder.utils.d
import com.iceteaviet.fastfoodfinder.utils.e
import com.iceteaviet.fastfoodfinder.utils.w
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(), View.OnClickListener {
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mCallBackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)

        // Initialize Firebase Auth
        if (dataManager.isSignedIn()) {
            // User is signed in
            this.finish()
            d(TAG, "onAuthStateChanged:signed_in:" + dataManager.getCurrentUserUid())
        } else {
            // User is signed out
            d(TAG, "onAuthStateChanged:signed_out")
        }

        mGoogleApiClient = setupGoogleSignIn()
        mCallBackManager = setupFacebookSignIn()

        btn_skip.setOnClickListener(this)
        btn_register.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        btn_google_signin.setOnClickListener(this)
    }

    override val layoutId: Int
        get() = R.layout.activity_login

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RETURN_CODE_GOOGLE_SIGN_IN) { // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with Firebase
                val account = result.signInAccount
                if (account != null)
                    authWithGoogle(account)
                else
                    e(TAG, "Login failed with google")
            } else {
                e(TAG, "Login failed with google")
            }
        } else {
            // Pass the activity result back to the Facebook SDK
            mCallBackManager!!.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_skip -> {
                // Anonymous mode
                startMainActivity()
            }

            R.id.btn_register -> {
                val dlg = EmailRegisterDialog.newInstance()
                dlg.show(supportFragmentManager, "dialog-email-register")
            }

            R.id.btn_login -> {
                val dlg = EmailLoginDialog.newInstance()
                dlg.show(supportFragmentManager, "dialog-email-login")
            }

            R.id.btn_google_signin -> {
                signIntWithGoogle(mGoogleApiClient)
            }
        }
    }

    private fun setupFacebookSignIn(): CallbackManager {
        val callbackManager = CallbackManager.Factory.create()
        btn_facebook_signin.setReadPermissions("email", "public_profile")

        btn_facebook_signin.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                d(TAG, "facebook:onSuccess: $loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                d(TAG, "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                d(TAG, "facebook:onError", error)
                // ...
            }
        })

        return callbackManager
    }

    private fun setupGoogleSignIn(): GoogleApiClient {
        val client: GoogleApiClient
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        client = GoogleApiClient.Builder(this@LoginActivity)
                .enableAutoManage(this@LoginActivity, GoogleApiClient.OnConnectionFailedListener { e(TAG, "google connect failed") })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        return client
    }


    private fun saveUserIfNotExists(user: User) {
        dataManager.getRemoteUserDataSource().insertOrUpdate(user)
        dataManager.setCurrentUser(user)
    }

    private fun startMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun authWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        dataManager.signInWithCredential(credential)
                .subscribe(object : SingleObserver<User> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(user: User) {
                        Toast.makeText(this@LoginActivity, R.string.sign_in_successfully, Toast.LENGTH_SHORT).show()
                        saveUserIfNotExists(user)
                        startMainActivity()
                    }

                    override fun onError(e: Throwable) {
                        w(TAG, "signInWithCredential", e)
                        Toast.makeText(this@LoginActivity, R.string.authentication_failed,
                                Toast.LENGTH_SHORT).show()
                    }
                })
    }

    private fun signIntWithGoogle(mGoogleApiClient: GoogleApiClient?) {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RETURN_CODE_GOOGLE_SIGN_IN)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        dataManager.signInWithCredential(credential)
                .subscribe(object : SingleObserver<User> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(user: User) {
                        Toast.makeText(this@LoginActivity, R.string.sign_in_successfully, Toast.LENGTH_SHORT).show()
                        saveUserIfNotExists(user)
                        startMainActivity()
                    }

                    override fun onError(e: Throwable) {
                        w(TAG, "signInWithCredential", e)
                        Toast.makeText(this@LoginActivity, R.string.authentication_failed,
                                Toast.LENGTH_SHORT).show()
                    }
                })
    }

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
        private const val RETURN_CODE_GOOGLE_SIGN_IN = 1
    }
}
