package com.iceteaviet.fastfoodfinder

import android.annotation.SuppressLint
import android.content.Context
import androidx.multidex.MultiDexApplication
import androidx.work.*
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.location.GoogleLocationManager
import com.iceteaviet.fastfoodfinder.location.SystemLocationManager
import com.iceteaviet.fastfoodfinder.service.eventbus.core.IBus
import com.iceteaviet.fastfoodfinder.service.workers.SyncDatabaseWorker
import com.iceteaviet.fastfoodfinder.utils.initLogger
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.ui.AppNotiManager
import java.util.concurrent.TimeUnit


/**
 * Created by tom on 7/15/18.
 */
class App : MultiDexApplication() {

    companion object {
        private const val SYNC_DB_JOB_TAG = "SYNC_DB_JOB_TAG"

        private lateinit var dataManager: DataManager
        private lateinit var schedulerProvider: SchedulerProvider
        private lateinit var bus: IBus

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
    }

    override fun onCreate() {
        super.onCreate()

        PACKAGE_NAME = applicationContext.packageName
        context = applicationContext

        initLogger()

        dataManager = Injection.provideDataManager()
        schedulerProvider = Injection.provideSchedulerProvider()
        bus = Injection.provideEventBus()

        GoogleLocationManager.init(getContext())
        SystemLocationManager.init(getContext())

        AppNotiManager.init(getContext())

        scheduleSyncDBWorker()
    }

    private fun scheduleSyncDBWorker() {
        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val myWorkBuilder = PeriodicWorkRequest.Builder(SyncDatabaseWorker::class.java, 1, TimeUnit.MINUTES)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.HOURS) // Backoff retry after 1 hour
        //.setConstraints(constraints)

        val myWork = myWorkBuilder.build()
        WorkManager.getInstance()
                .enqueueUniquePeriodicWork(SYNC_DB_JOB_TAG, ExistingPeriodicWorkPolicy.KEEP, myWork)
    }

    override fun onTerminate() {
        GoogleLocationManager.getInstance().terminate()
        SystemLocationManager.getInstance().terminate()
        super.onTerminate()
    }
}
