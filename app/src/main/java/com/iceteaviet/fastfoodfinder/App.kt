package com.iceteaviet.fastfoodfinder

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.location.GoogleLocationManager
import com.iceteaviet.fastfoodfinder.location.SystemLocationManager
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
        schedulerProvider = Injection.provideSchedulerProvider()
        bus = Injection.provideEventBus()
        notiManager = AppNotiManager(getContext())

        GoogleLocationManager.init(getContext())
        SystemLocationManager.init(getContext())

        scheduleSyncDBWorker()
    }

    private fun scheduleSyncDBWorker() {
        val work = SyncDatabaseWorker.prepareSyncDBWorker()
        WorkManager.getInstance()
            .enqueueUniquePeriodicWork(SYNC_DB_JOB_TAG, ExistingPeriodicWorkPolicy.KEEP, work)
    }


    override fun onTerminate() {
        GoogleLocationManager.getInstance().terminate()
        SystemLocationManager.getInstance().terminate()
        super.onTerminate()
    }
}
