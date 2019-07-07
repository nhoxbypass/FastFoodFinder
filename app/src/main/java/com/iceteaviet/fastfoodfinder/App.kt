package com.iceteaviet.fastfoodfinder

import android.annotation.SuppressLint
import android.content.Context
import androidx.multidex.MultiDexApplication
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.location.GoogleLocationManager
import com.iceteaviet.fastfoodfinder.location.SystemLocationManager
import com.iceteaviet.fastfoodfinder.service.eventbus.core.IBus
import com.iceteaviet.fastfoodfinder.service.workers.SyncDatabaseWorker
import com.iceteaviet.fastfoodfinder.utils.initLogger
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.ui.AppNotiManager
import com.iceteaviet.fastfoodfinder.utils.ui.NotiManager


/**
 * Created by tom on 7/15/18.
 */
class App : MultiDexApplication() {

    companion object {
        private const val SYNC_DB_JOB_TAG = "SYNC_DB_JOB_TAG"

        private lateinit var dataManager: DataManager
        private lateinit var schedulerProvider: SchedulerProvider
        private lateinit var bus: IBus
        private lateinit var notiManager: NotiManager

        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context
        lateinit var PACKAGE_NAME: String

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
