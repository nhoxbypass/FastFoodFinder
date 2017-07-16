package com.iceteaviet.fastfoodfinder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.iceteaviet.fastfoodfinder.model.Store.UserStoreList;
import com.iceteaviet.fastfoodfinder.model.User.User;
import com.iceteaviet.fastfoodfinder.rest.FirebaseClient;
import com.iceteaviet.fastfoodfinder.utils.Constant;
import com.iceteaviet.fastfoodfinder.R;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
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
    private FirebaseAuth mAuth;
    private boolean isAuthenticated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            // User is signed in
            this.finish();
            Log.d("MAPP", "onAuthStateChanged:signed_in:" + mAuth.getCurrentUser().getUid());
        } else {
            // User is signed out
            Log.d("MAPP", "onAuthStateChanged:signed_out");
        }

        mGoogleApiClient = setupGoogleSignIn();

        mCallBackManager = setupFacebookSignIn();

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMainActivity();
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        fbSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.e("MAPP", "Login failed with google");
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
                Log.d("MAPP", "facebook:onSuccess: " + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("MAPP", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("MAPP", "facebook:onError", error);
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
                        Log.e("MAPP", "google connect failed");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        return client;
    }


    private void saveUserIfNotExists(FirebaseUser firebaseUser) {
        String photoUrl = "http://cdn.builtlean.com/wp-content/uploads/2015/11/all_noavatar.png.png";

        if (firebaseUser.getPhotoUrl() != null) {
            photoUrl = firebaseUser.getPhotoUrl().toString();
        }

        User.currentUser = new User(firebaseUser.getDisplayName(), firebaseUser.getEmail(), photoUrl, firebaseUser.getUid(), new ArrayList<UserStoreList>());
        User.currentUser.saveUserIfNotExists();
    }

    private void startMainActivity() {
        final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        if (isAuthenticated) {
            FirebaseClient.getInstance().addListenerForSingleUserValueEvent(User.currentUser.getUid(), new FirebaseClient.UserValueEventListener() {
                @Override
                public void onDataChange(User user) {
                    User.currentUser = user;
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }
        else {
            startActivity(intent);
            finish();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("MAPP", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("MAPP", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.authentication_failed,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.sign_in_successfully, Toast.LENGTH_SHORT).show();
                            saveUserIfNotExists(task.getResult().getUser());
                            isAuthenticated = true;
                            startMainActivity();
                        }
                    }
                });
    }

    private void signIntWithGoogle(GoogleApiClient mGoogleApiClient) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("MAPP", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("MAPP", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.authentication_failed,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            isAuthenticated = true;
                            Toast.makeText(LoginActivity.this, R.string.sign_in_successfully, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
