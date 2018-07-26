package com.iceteaviet.fastfoodfinder.data;

import android.app.Activity;
import android.util.Log;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreDataSource;
import com.iceteaviet.fastfoodfinder.data.domain.user.UserDataSource;
import com.iceteaviet.fastfoodfinder.data.prefs.PreferencesHelper;
import com.iceteaviet.fastfoodfinder.data.remote.ClientAuth;
import com.iceteaviet.fastfoodfinder.data.remote.routing.MapsRoutingApiHelper;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User;
import com.iceteaviet.fastfoodfinder.utils.Constant;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;

/**
 * Created by tom on 7/9/18.
 */

// TODO: Migration realm
public class AppDataManager implements DataManager {
    private static final String TAG = AppDataManager.class.getSimpleName();

    private StoreDataSource localStoreDataSource;
    private StoreDataSource remoteStoreDataSource;
    private ClientAuth clientAuth;
    private UserDataSource localUserDataSource;
    private UserDataSource remoteUserDataSource;
    private MapsRoutingApiHelper mapsRoutingApiHelper;
    private PreferencesHelper preferencesHelper;

    private User currentUser;

    public AppDataManager(StoreDataSource storeDataSource, StoreDataSource remoteStoreDataSource,
                          ClientAuth clientAuth,
                          UserDataSource userDataSource, UserDataSource remoteUserDataSource,
                          MapsRoutingApiHelper mapsRoutingApiHelper, PreferencesHelper preferencesHelper) {
        this.localStoreDataSource = storeDataSource;
        this.remoteStoreDataSource = remoteStoreDataSource;
        this.clientAuth = clientAuth;
        this.localUserDataSource = userDataSource;
        this.remoteUserDataSource = remoteUserDataSource;
        this.mapsRoutingApiHelper = mapsRoutingApiHelper;
        this.preferencesHelper = preferencesHelper;
    }

    @Override
    public StoreDataSource getLocalStoreDataSource() {
        return localStoreDataSource;
    }

    @Override
    public StoreDataSource getRemoteStoreDataSource() {
        return remoteStoreDataSource;
    }

    @Override
    public UserDataSource getLocalUserDataSource() {
        return localUserDataSource;
    }

    @Override
    public UserDataSource getRemoteUserDataSource() {
        return remoteUserDataSource;
    }

    @Override
    public MapsRoutingApiHelper getMapsRoutingApiHelper() {
        return mapsRoutingApiHelper;
    }

    @Override
    public PreferencesHelper getPreferencesHelper() {
        return preferencesHelper;
    }

    @Override
    public Single<List<Store>> loadStoresFromServer(Activity activity) {
        return Single.create(new SingleOnSubscribe<List<Store>>() {
            @Override
            public void subscribe(final SingleEmitter<List<Store>> emitter) throws Exception {
                if (!clientAuth.isSignedIn()) {
                    // Not signed in
                    clientAuth.signInWithEmailAndPassword(Constant.DOWNLOADER_BOT_EMAIL, Constant.DOWNLOADER_BOT_PWD)
                            .subscribe(new SingleObserver<Boolean>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onSuccess(Boolean b) {
                                    if (b) {
                                        remoteStoreDataSource.getAllStores()
                                                .subscribe(new SingleObserver<List<Store>>() {
                                                    @Override
                                                    public void onSubscribe(Disposable d) {

                                                    }

                                                    @Override
                                                    public void onSuccess(List<Store> storeList) {
                                                        emitter.onSuccess(storeList);
                                                    }

                                                    @Override
                                                    public void onError(Throwable e) {
                                                        emitter.onError(e);
                                                    }
                                                });
                                    } else {

                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.w(TAG, "Sign In to Get data ", e);
                                    emitter.onError(e);
                                }
                            });
                } else {
                    remoteStoreDataSource.getAllStores()
                            .subscribe(new SingleObserver<List<Store>>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onSuccess(List<Store> storeList) {
                                    emitter.onSuccess(storeList);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    emitter.onError(e);
                                }
                            });
                }
            }
        });
    }

    @Override
    public String getCurrentUserUid() {
        String uid = clientAuth.getCurrentUserUid();
        if (uid == null || uid.isEmpty())
            uid = preferencesHelper.getCurrentUserUid();

        return uid;
    }

    @Override
    public boolean isSignedIn() {
        return clientAuth.isSignedIn();
    }

    @Override
    public void signOut() {
        clientAuth.signOut();
    }

    @Override
    public Single<Boolean> signInWithEmailAndPassword(String email, String password) {
        return clientAuth.signInWithEmailAndPassword(email, password);
    }

    @Override
    public Single<FirebaseUser> signInWithCredential(AuthCredential authCredential) {
        return clientAuth.signInWithCredential(authCredential);
    }

    @Override
    public User getCurrentUser() {
        if (currentUser == null)
            currentUser = localUserDataSource.getUser(getCurrentUserUid())
                    .blockingGet();

        return currentUser;
    }

    @Override
    public void setCurrentUser(User user) {
        currentUser = user;
        preferencesHelper.setCurrentUserUid(user.getUid());
        localUserDataSource.insertOrUpdate(user);
    }
}
