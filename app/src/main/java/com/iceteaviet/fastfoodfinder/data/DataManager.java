package com.iceteaviet.fastfoodfinder.data;

import android.app.Activity;

import com.iceteaviet.fastfoodfinder.data.base.store.StoreDataSource;
import com.iceteaviet.fastfoodfinder.data.remote.ClientAuth;
import com.iceteaviet.fastfoodfinder.data.remote.routing.MapsRoutingApiHelper;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;
import com.iceteaviet.fastfoodfinder.data.remote.user.UserDataSource;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by tom on 7/9/18.
 */
public interface DataManager extends ClientAuth {
    StoreDataSource getLocalStoreDataSource();

    StoreDataSource getRemoteStoreDataSource();

    UserDataSource getUserDataSource();

    MapsRoutingApiHelper getMapsRoutingApiHelper();

    Single<List<Store>> readDataFromFirebase(Activity activity);
}
