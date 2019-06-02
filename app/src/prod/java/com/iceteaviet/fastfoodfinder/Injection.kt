package com.iceteaviet.fastfoodfinder

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.iceteaviet.fastfoodfinder.data.AppDataManager
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.auth.FirebaseClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.prefs.AppPreferencesRepository
import com.iceteaviet.fastfoodfinder.data.domain.prefs.PreferencesRepository
import com.iceteaviet.fastfoodfinder.data.domain.routing.AppMapsRoutingRepository
import com.iceteaviet.fastfoodfinder.data.domain.routing.MapsRoutingRepository
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
import com.iceteaviet.fastfoodfinder.data.local.prefs.PreferencesWrapper
import com.iceteaviet.fastfoodfinder.data.remote.routing.GoogleMapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.MapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.store.FirebaseStoreApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.store.StoreApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.user.FirebaseUserApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.user.UserApiHelper
import com.iceteaviet.fastfoodfinder.utils.rx.AppSchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider

/**
 * Enables injection of production implementations at compile time.
 */
object Injection {
    fun provideDataManager(): DataManager {
        return AppDataManager(provideContext(), provideStoreRepository(), provideUserRepository(),
                provideAuthClient(),
                provideRoutingRepository(), providePreferenceRepository())
    }

    fun provideSchedulerProvider(): SchedulerProvider {
        return AppSchedulerProvider()
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

    fun provideRemoteStoreDataSource(): StoreApiHelper {
        return FirebaseStoreApiHelper(FirebaseDatabase.getInstance().reference)
    }

    fun provideRemoteUserDataSource(): UserApiHelper {
        return FirebaseUserApiHelper(FirebaseDatabase.getInstance().reference)
    }

    fun provideRoutingRepository(): MapsRoutingRepository {
        return AppMapsRoutingRepository(provideRoutingApiHelper())
    }

    fun provideRoutingApiHelper(): MapsRoutingApiHelper {
        return GoogleMapsRoutingApiHelper(App.getContext().getString(R.string.google_maps_browser_key))
    }

    fun providePreferenceRepository(): PreferencesRepository {
        return AppPreferencesRepository(Injection.providePreferenceHelper())
    }

    fun providePreferenceHelper(): PreferencesHelper {
        return AppPreferencesHelper(providePreferenceWrapper())
    }

    fun providePreferenceWrapper(): PreferencesWrapper {
        return AppPreferencesWrapper(App.getContext())
    }

    fun provideAuthClient(): ClientAuth {
        return FirebaseClientAuth()
    }
}