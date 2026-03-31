package com.iceteaviet.fastfoodfinder

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.iceteaviet.fastfoodfinder.core.location.GoogleLocationManager
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.core.location.SystemLocationManager
import com.iceteaviet.fastfoodfinder.service.eventbus.core.IBus
import com.iceteaviet.fastfoodfinder.service.workers.SyncDatabaseWorker
import com.iceteaviet.fastfoodfinder.utils.getAppSignatureSHA1
import com.iceteaviet.fastfoodfinder.utils.initLogger
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.ui.AppNotiManager
import com.iceteaviet.fastfoodfinder.utils.ui.NotiManager


/**
 * Created by tom on 7/15/18.
 */
class App : Application() {

    companion object {
        private const val SYNC_DB_JOB_TAG = "SYNC_DB_JOB_TAG"

        private lateinit var dataManager: DataManager
        private lateinit var schedulerProvider: SchedulerProvider
        private lateinit var bus: IBus
        private lateinit var notiManager: NotiManager

        private lateinit var storeRepository: com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
        private lateinit var userRepository: com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository
        private lateinit var mapsRoutingRepository: com.iceteaviet.fastfoodfinder.data.domain.routing.MapsRoutingRepository
        private lateinit var preferencesRepository: com.iceteaviet.fastfoodfinder.data.domain.prefs.PreferencesRepository
        private lateinit var clientAuth: com.iceteaviet.fastfoodfinder.data.auth.ClientAuth

        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        @VisibleForTesting
        lateinit var PACKAGE_NAME: String

        @VisibleForTesting
        lateinit var SHA1: String

        fun getPackageName(): String {
            return PACKAGE_NAME
        }

        fun getSignatureSHA1(): String {
            return SHA1
        }

        fun getDataManager(): DataManager {
            return dataManager
        }

        fun getStoreRepository(): com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository {
            return storeRepository
        }

        fun getUserRepository(): com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository {
            return userRepository
        }

        fun getMapsRoutingRepository(): com.iceteaviet.fastfoodfinder.data.domain.routing.MapsRoutingRepository {
            return mapsRoutingRepository
        }

        fun getPreferencesRepository(): com.iceteaviet.fastfoodfinder.data.domain.prefs.PreferencesRepository {
            return preferencesRepository
        }
        
        fun getClientAuth(): com.iceteaviet.fastfoodfinder.data.auth.ClientAuth {
            return clientAuth
        }

        fun getSchedulerProvider(): SchedulerProvider {
            return schedulerProvider
        }

        fun getContext(): Context {
            return context
        }

        fun getBus(): IBus {
            return bus
        }

        fun getNotiManager(): NotiManager {
            return notiManager
        }
    }

    override fun onCreate() {
        super.onCreate()

        PACKAGE_NAME = applicationContext.packageName
        SHA1 = getAppSignatureSHA1(this)
        context = applicationContext

        initLogger()

        dataManager = Injection.provideDataManager()
        
        storeRepository = Injection.provideStoreRepository()
        userRepository = Injection.provideUserRepository()
        mapsRoutingRepository = Injection.provideRoutingRepository()
        preferencesRepository = Injection.providePreferenceRepository()
        clientAuth = Injection.provideAuthClient()

        schedulerProvider = Injection.provideSchedulerProvider()
        bus = Injection.provideEventBus()
        notiManager = AppNotiManager(getContext())

        dataManager.initialize(getContext())

        GoogleLocationManager.init(getContext())
        SystemLocationManager.init(getContext())

        scheduleSyncDBWorker()
    }

    private fun scheduleSyncDBWorker() {
        val work = SyncDatabaseWorker.prepareSyncDBWorker()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(SYNC_DB_JOB_TAG, ExistingPeriodicWorkPolicy.KEEP, work)
    }


    override fun onTerminate() {
        GoogleLocationManager.getInstance().terminate()
        SystemLocationManager.getInstance().terminate()
        super.onTerminate()
    }
}
