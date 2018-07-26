package com.iceteaviet.fastfoodfinder.data;

import android.app.Activity;

import com.iceteaviet.fastfoodfinder.data.domain.store.StoreDataSource;
import com.iceteaviet.fastfoodfinder.data.domain.user.UserDataSource;
import com.iceteaviet.fastfoodfinder.data.prefs.PreferencesHelper;
import com.iceteaviet.fastfoodfinder.data.remote.ClientAuth;
import com.iceteaviet.fastfoodfinder.data.remote.routing.MapsRoutingApiHelper;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by tom on 7/9/18.
 */
public interface DataManager extends ClientAuth {
    StoreDataSource getLocalStoreDataSource();

    StoreDataSource getRemoteStoreDataSource();

    UserDataSource getRemoteUserDataSource();

    UserDataSource getLocalUserDataSource();

    MapsRoutingApiHelper getMapsRoutingApiHelper();

    PreferencesHelper getPreferencesHelper();

    Single<List<Store>> loadStoresFromServer(Activity activity);

    User getCurrentUser();

    void setCurrentUser(User user);
}
