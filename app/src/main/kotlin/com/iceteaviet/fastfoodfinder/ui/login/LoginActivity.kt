package com.iceteaviet.fastfoodfinder.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.ui.main.MainActivity
import com.iceteaviet.fastfoodfinder.utils.*
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var skipButton: Button
    lateinit var joinNowButton: Button
    lateinit var fbSignInButton: LoginButton
    lateinit var googleSignInButton: SignInButton

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mCallBackManager: CallbackManager? = null
    private lateinit var dataManager: DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.activity_login)

        skipButton = btn_skip
        joinNowButton = btn_join_now
        fbSignInButton = btn_facebook_signin
        googleSignInButton = btn_google_signin


        dataManager = App.getDataManager()

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

        skipButton.setOnClickListener {
            // Anonymous mode
            startMainActivity()
        }

        joinNowButton.setOnClickListener {
            // TODO: Support email login
        }

        googleSignInButton.setOnClickListener { signIntWithGoogle(mGoogleApiClient) }
    }

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


    private fun setupFacebookSignIn(): CallbackManager {
        val callbackManager = CallbackManager.Factory.create()
        fbSignInButton.setReadPermissions("email", "public_profile")

        fbSignInButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
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


    private fun saveUserIfNotExists(firebaseUser: FirebaseUser) {
        var photoUrl = Constant.NO_AVATAR_PLACEHOLDER_URL

        if (firebaseUser.photoUrl != null) {
            photoUrl = firebaseUser.photoUrl!!.toString()
        }

        val user = User(firebaseUser.displayName!!, firebaseUser.email!!, photoUrl, firebaseUser.uid, getDefaultUserStoreLists().toMutableList())
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
                .subscribe(object : SingleObserver<FirebaseUser> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(firebaseUser: FirebaseUser) {
                        if (firebaseUser != null) {
                            Toast.makeText(this@LoginActivity, R.string.sign_in_successfully, Toast.LENGTH_SHORT).show()
                            saveUserIfNotExists(firebaseUser)
                            startMainActivity()
                        } else {
                            w(TAG, "signInWithCredential")
                            Toast.makeText(this@LoginActivity, R.string.authentication_failed,
                                    Toast.LENGTH_SHORT).show()
                        }
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
                .subscribe(object : SingleObserver<FirebaseUser> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(firebaseUser: FirebaseUser) {
                        if (firebaseUser != null) {
                            Toast.makeText(this@LoginActivity, R.string.sign_in_successfully, Toast.LENGTH_SHORT).show()
                            saveUserIfNotExists(firebaseUser)
                            startMainActivity()
                        } else {
                            w(TAG, "signInWithCredential")
                            Toast.makeText(this@LoginActivity, R.string.authentication_failed,
                                    Toast.LENGTH_SHORT).show()
                        }
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
        private val RETURN_CODE_GOOGLE_SIGN_IN = 1
    }
}
