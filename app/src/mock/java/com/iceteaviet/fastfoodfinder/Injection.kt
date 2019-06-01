package com.iceteaviet.fastfoodfinder

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.FakeDataManager
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.auth.FakeFirebaseClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.store.AppStoreRepository
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.data.domain.user.AppUserRepository
import com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository
import com.iceteaviet.fastfoodfinder.data.local.db.store.FakeLocalStoreRepository
import com.iceteaviet.fastfoodfinder.data.local.db.store.StoreDataSource
import com.iceteaviet.fastfoodfinder.data.local.db.user.FakeLocalUserRepository
import com.iceteaviet.fastfoodfinder.data.local.db.user.UserDataSource
import com.iceteaviet.fastfoodfinder.data.local.prefs.AppPreferencesHelper
import com.iceteaviet.fastfoodfinder.data.local.prefs.FakePreferencesHelper
import com.iceteaviet.fastfoodfinder.data.local.prefs.PreferencesHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.FakeGoogleMapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.MapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.store.FakeFirebaseStoreRepository
import com.iceteaviet.fastfoodfinder.data.remote.store.StoreApi
import com.iceteaviet.fastfoodfinder.data.remote.user.FakeFirebaseUserRepository
import com.iceteaviet.fastfoodfinder.data.remote.user.UserApi

/**
 * Enables injection of mock implementations at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
object Injection {
    fun provideDataManager(): DataManager {
        return FakeDataManager(provideStoreRepository(), provideUserRepository(),
                provideAuthClient(),
                provideRoutingApiHelper(), providePreferenceHelper())
    }

    fun provideStoreRepository(): StoreRepository {
        return AppStoreRepository(provideRemoteStoreDataSource(), provideLocalStoreDataSource())
    }

    fun provideUserRepository(): UserRepository {
        return AppUserRepository(provideRemoteUserDataSource(), provideLocalUserDataSource())
    }

    fun provideLocalStoreDataSource(): StoreDataSource {
        return FakeLocalStoreRepository()
    }

    fun provideLocalUserDataSource(): UserDataSource {
        return FakeLocalUserRepository()
    }

    fun provideRemoteStoreDataSource(): StoreApi {
        return FakeFirebaseStoreRepository()
    }

    fun provideRemoteUserDataSource(): UserApi {
        return FakeFirebaseUserRepository()
    }

    fun provideRoutingApiHelper(): MapsRoutingApiHelper {
        return FakeGoogleMapsRoutingApiHelper()
    }

    fun providePreferenceHelper(): PreferencesHelper {
        return AppPreferencesHelper(FakePreferencesHelper())
    }

    fun provideAuthClient(): ClientAuth {
        return FakeFirebaseClientAuth()
    }
}