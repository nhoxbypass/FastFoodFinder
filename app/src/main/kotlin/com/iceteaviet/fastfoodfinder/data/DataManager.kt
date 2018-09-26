package com.iceteaviet.fastfoodfinder.data

import android.app.Activity

import com.iceteaviet.fastfoodfinder.data.domain.store.StoreDataSource
import com.iceteaviet.fastfoodfinder.data.domain.user.UserDataSource
import com.iceteaviet.fastfoodfinder.data.prefs.PreferencesHelper
import com.iceteaviet.fastfoodfinder.data.remote.ClientAuth
import com.iceteaviet.fastfoodfinder.data.remote.routing.MapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User

import io.reactivex.Single

/**
 * Created by tom on 7/9/18.
 */
interface DataManager : ClientAuth {
    fun getLocalStoreDataSource(): StoreDataSource

    fun getRemoteStoreDataSource(): StoreDataSource

    fun getRemoteUserDataSource(): UserDataSource

    fun getLocalUserDataSource(): UserDataSource

    fun getMapsRoutingApiHelper(): MapsRoutingApiHelper

    fun getPreferencesHelper(): PreferencesHelper

    fun getCurrentUser(): User?

    fun loadStoresFromServer(activity: Activity): Single<List<Store>>

    fun setCurrentUser(user: User?)
}
