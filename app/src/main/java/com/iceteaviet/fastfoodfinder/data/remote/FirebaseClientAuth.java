package com.iceteaviet.fastfoodfinder.data.remote;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * Created by Genius Doan on 14/07/2017.
 */

public class FirebaseClientAuth implements ClientAuth {
    private FirebaseAuth mAuth;
    private User currentUser;

    public FirebaseClientAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public String getCurrentUserUid() {
        if (mAuth.getCurrentUser() != null)
            return mAuth.getCurrentUser().getUid();
        return null;
    }

    @Override
    public boolean isSignedIn() {
        return mAuth.getCurrentUser() != null && !mAuth.getCurrentUser().isAnonymous();
    }

    @Override
    public void signOut() {
        mAuth.signOut();
    }

    @Override
    public Single<Boolean> signInWithEmailAndPassword(final String email, final String password) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final SingleEmitter<Boolean> emitter) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                emitter.onSuccess(true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                emitter.onError(e);
                            }
                        })
                        .addOnCanceledListener(new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                emitter.onError(new Exception("Cancel"));
                            }
                        });
            }
        });
    }

    @Override
    public Single<FirebaseUser> signInWithCredential(final AuthCredential authCredential) {
        return Single.create(new SingleOnSubscribe<FirebaseUser>() {
            @Override
            public void subscribe(final SingleEmitter<FirebaseUser> emitter) {
                mAuth.signInWithCredential(authCredential)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                emitter.onSuccess(authResult.getUser());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                emitter.onError(e);
                            }
                        })
                        .addOnCanceledListener(new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                emitter.onError(new Exception("Cancel"));
                            }
                        });
            }
        });
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void setCurrentUser(User user) {
        currentUser = user;
    }
}
