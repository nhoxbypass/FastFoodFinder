package com.iceteaviet.fastfoodfinder.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.iceteaviet.fastfoodfinder.App;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.data.DataManager;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User;
import com.iceteaviet.fastfoodfinder.ui.main.MainActivity;
import com.iceteaviet.fastfoodfinder.utils.AppLogger;
import com.iceteaviet.fastfoodfinder.utils.Constant;
import com.iceteaviet.fastfoodfinder.utils.DataUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RETURN_CODE_GOOGLE_SIGN_IN = 1;

    @BindView(R.id.btn_skip)
    Button skipButton;
    @BindView(R.id.btn_join_now)
    Button joinNowButton;
    @BindView(R.id.btn_facebook_signin)
    LoginButton fbSignInButton;
    @BindView(R.id.btn_google_signin)
    SignInButton googleSignInButton;

    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mCallBackManager;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        dataManager = App.getDataManager();

        // Initialize Firebase Auth
        if (dataManager.isSignedIn()) {
            // User is signed in
            this.finish();
            AppLogger.d(TAG, "onAuthStateChanged:signed_in:" + dataManager.getCurrentUserUid());
        } else {
            // User is signed out
            AppLogger.d(TAG, "onAuthStateChanged:signed_out");
        }

        mGoogleApiClient = setupGoogleSignIn();
        mCallBackManager = setupFacebookSignIn();

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Anonymous mode
                startMainActivity();
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Support email login
            }
        });

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIntWithGoogle(mGoogleApiClient);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RETURN_CODE_GOOGLE_SIGN_IN) { // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                if (account != null)
                    authWithGoogle(account);
                else
                    AppLogger.e(TAG, "Login failed with google");
            } else {
                AppLogger.e(TAG, "Login failed with google");
            }
        } else {
            // Pass the activity result back to the Facebook SDK
            mCallBackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private CallbackManager setupFacebookSignIn() {
        CallbackManager callbackManager = CallbackManager.Factory.create();
        fbSignInButton.setReadPermissions("email", "public_profile");

        fbSignInButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AppLogger.d(TAG, "facebook:onSuccess: " + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                AppLogger.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                AppLogger.d(TAG, "facebook:onError", error);
                // ...
            }
        });

        return callbackManager;
    }

    private GoogleApiClient setupGoogleSignIn() {
        GoogleApiClient client;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        client = new GoogleApiClient.Builder(LoginActivity.this)
                .enableAutoManage(LoginActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        AppLogger.e(TAG, "google connect failed");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        return client;
    }


    private void saveUserIfNotExists(FirebaseUser firebaseUser) {
        String photoUrl = Constant.NO_AVATAR_PLACEHOLDER_URL;

        if (firebaseUser.getPhotoUrl() != null) {
            photoUrl = firebaseUser.getPhotoUrl().toString();
        }

        User user = new User(firebaseUser.getDisplayName(), firebaseUser.getEmail(), photoUrl, firebaseUser.getUid(), DataUtils.getDefaultUserStoreLists());
        dataManager.getRemoteUserDataSource().insertOrUpdate(user);
        dataManager.setCurrentUser(user);
    }

    private void startMainActivity() {
        final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void authWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        dataManager.signInWithCredential(credential)
                .subscribe(new SingleObserver<FirebaseUser>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(FirebaseUser firebaseUser) {
                        if (firebaseUser != null) {
                            Toast.makeText(LoginActivity.this, R.string.sign_in_successfully, Toast.LENGTH_SHORT).show();
                            saveUserIfNotExists(firebaseUser);
                            startMainActivity();
                        } else {
                            AppLogger.w(TAG, "signInWithCredential");
                            Toast.makeText(LoginActivity.this, R.string.authentication_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        AppLogger.w(TAG, "signInWithCredential", e);
                        Toast.makeText(LoginActivity.this, R.string.authentication_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signIntWithGoogle(GoogleApiClient mGoogleApiClient) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RETURN_CODE_GOOGLE_SIGN_IN);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        dataManager.signInWithCredential(credential)
                .subscribe(new SingleObserver<FirebaseUser>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(FirebaseUser firebaseUser) {
                        if (firebaseUser != null) {
                            Toast.makeText(LoginActivity.this, R.string.sign_in_successfully, Toast.LENGTH_SHORT).show();
                            saveUserIfNotExists(firebaseUser);
                            startMainActivity();
                        } else {
                            AppLogger.w(TAG, "signInWithCredential");
                            Toast.makeText(LoginActivity.this, R.string.authentication_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        AppLogger.w(TAG, "signInWithCredential", e);
                        Toast.makeText(LoginActivity.this, R.string.authentication_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
