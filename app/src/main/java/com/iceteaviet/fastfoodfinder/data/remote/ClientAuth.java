package com.iceteaviet.fastfoodfinder.data.remote;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User;

import io.reactivex.Single;

/**
 * Created by tom on 7/17/18.
 */
public interface ClientAuth {
    String getCurrentUserUid();

    boolean isSignedIn();

    void signOut();

    Single<Boolean> signInWithEmailAndPassword(String email, String password);

    Single<FirebaseUser> signInWithCredential(AuthCredential authCredential);

    User getCurrentUser();

    void setCurrentUser(User user);
}
