package com.iceteaviet.fastfoodfinder.di

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.iceteaviet.fastfoodfinder.R
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
import com.iceteaviet.fastfoodfinder.data.local.db.store.StoreDao
import com.iceteaviet.fastfoodfinder.data.local.db.user.UserDao
import com.iceteaviet.fastfoodfinder.data.local.prefs.AppPreferencesHelper
import com.iceteaviet.fastfoodfinder.data.local.prefs.AppPreferencesWrapper
import com.iceteaviet.fastfoodfinder.data.local.prefs.AppPreferencesWrapper.Companion.PREFS_NAME
import com.iceteaviet.fastfoodfinder.data.remote.routing.GoogleMapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.store.FirebaseStoreApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.user.FirebaseUserApiHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideStoreRepository(storeDao: StoreDao): StoreRepository {
        val remote = FirebaseStoreApiHelper(FirebaseDatabase.getInstance().reference)
        return AppStoreRepository(remote, storeDao)
    }

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository {
        val remote = FirebaseUserApiHelper(FirebaseDatabase.getInstance().reference)
        return AppUserRepository(remote, userDao)
    }

    @Provides
    @Singleton
    fun provideRoutingRepository(@ApplicationContext context: Context): MapsRoutingRepository {
        val apiHelper = GoogleMapsRoutingApiHelper(context.getString(R.string.google_maps_browser_key))
        return AppMapsRoutingRepository(apiHelper)
    }

    @Provides
    @Singleton
    fun providePreferenceRepository(@ApplicationContext context: Context): PreferencesRepository {
        val wrapper = AppPreferencesWrapper(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE))
        val helper = AppPreferencesHelper(wrapper)
        return AppPreferencesRepository(helper)
    }

    @Provides
    @Singleton
    fun provideAuthClient(): ClientAuth {
        return FirebaseClientAuth()
    }
}
