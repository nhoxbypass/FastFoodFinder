package com.iceteaviet.fastfoodfinder

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.google.android.gms.maps.MapsInitializer
import com.iceteaviet.fastfoodfinder.core.location.GoogleLocationManager
import com.iceteaviet.fastfoodfinder.core.location.SystemLocationManager
import com.iceteaviet.fastfoodfinder.service.workers.SyncDatabaseWorker
import com.iceteaviet.fastfoodfinder.utils.getAppSignatureSHA1
import com.iceteaviet.fastfoodfinder.utils.initLogger
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Created by tom on 7/15/18.
 */
@HiltAndroidApp
class App : Application() {

    companion object {
        const val SYNC_DB_JOB_TAG = "SYNC_DB_JOB_TAG"

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

        fun getContext(): Context {
            return context
        }
    }

    override fun onCreate() {
        super.onCreate()

        PACKAGE_NAME = applicationContext.packageName
        SHA1 = getAppSignatureSHA1(this)
        context = applicationContext

        initLogger()

        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST) {
            when (it) {
                MapsInitializer.Renderer.LATEST -> Timber.d("The latest version of the renderer is used.")
                MapsInitializer.Renderer.LEGACY -> Timber.d("The legacy version of the renderer is used.")
            }
        }

        com.iceteaviet.fastfoodfinder.utils.DatabaseInitializer.init(getContext())

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
