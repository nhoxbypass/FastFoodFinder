package com.iceteaviet.fastfoodfinder

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.iceteaviet.fastfoodfinder.data.AppDataManager
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.auth.FirebaseClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.store.AppStoreRepository
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.data.domain.user.AppUserRepository
import com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository
import com.iceteaviet.fastfoodfinder.data.local.db.store.StoreDAO
import com.iceteaviet.fastfoodfinder.data.local.db.store.StoreDataSource
import com.iceteaviet.fastfoodfinder.data.local.db.user.UserDAO
import com.iceteaviet.fastfoodfinder.data.local.db.user.UserDataSource
import com.iceteaviet.fastfoodfinder.data.local.prefs.AppPreferencesHelper
import com.iceteaviet.fastfoodfinder.data.local.prefs.AppPreferencesWrapper
import com.iceteaviet.fastfoodfinder.data.local.prefs.PreferencesHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.GoogleMapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.MapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.store.FirebaseStoreApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.store.StoreApi
import com.iceteaviet.fastfoodfinder.data.remote.user.FirebaseUserApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.user.UserApi

/**
 * Enables injection of production implementations at compile time.
 */
object Injection {
    fun provideDataManager(): DataManager {
        return AppDataManager(provideContext(), provideStoreRepository(), provideUserRepository(),
                provideAuthClient(),
                provideRoutingApiHelper(), providePreferenceHelper())
    }

    fun provideContext(): Context {
        return App.getContext()
    }

    fun provideStoreRepository(): StoreRepository {
        return AppStoreRepository(provideRemoteStoreDataSource(), provideLocalStoreDataSource())
    }

    fun provideUserRepository(): UserRepository {
        return AppUserRepository(provideRemoteUserDataSource(), provideLocalUserDataSource())
    }

    fun provideLocalStoreDataSource(): StoreDataSource {
        return StoreDAO()
    }

    fun provideLocalUserDataSource(): UserDataSource {
        return UserDAO()
    }

    fun provideRemoteStoreDataSource(): StoreApi {
        return FirebaseStoreApiHelper(FirebaseDatabase.getInstance().reference)
    }

    fun provideRemoteUserDataSource(): UserApi {
        return FirebaseUserApiHelper(FirebaseDatabase.getInstance().reference)
    }

    fun provideRoutingApiHelper(): MapsRoutingApiHelper {
        return GoogleMapsRoutingApiHelper(App.getContext().getString(R.string.google_maps_browser_key))
    }

    fun providePreferenceHelper(): PreferencesHelper {
        return AppPreferencesHelper(AppPreferencesWrapper(App.getContext()))
    }

    fun provideAuthClient(): ClientAuth {
        return FirebaseClientAuth()
    }
}