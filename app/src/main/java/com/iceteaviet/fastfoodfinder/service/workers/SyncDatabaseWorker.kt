package com.iceteaviet.fastfoodfinder.service.workers

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkerParameters
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRefreshService
import com.iceteaviet.fastfoodfinder.utils.ui.NotiManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

class SyncDatabaseWorker @AssistedInject constructor(
    @Assisted val ctx: Context,
    @Assisted val params: WorkerParameters,
    private val storeRefreshService: StoreRefreshService,
    private val notiManager: NotiManager,
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        notiManager.showStoreSyncProgressStatusNotification(
            applicationContext.getString(R.string.str_updating_store_db),
            applicationContext.getString(R.string.str_update_app_db)
        )

        return try {
            val storeList = storeRefreshService.refreshStoresFromRemote()

            if (storeList.isNotEmpty()) {
                notiManager.showStoreSyncStatusNotification(
                    String.format(applicationContext.getString(R.string.update_database_successfull_with_count), storeList.size),
                    applicationContext.getString(R.string.str_update_app_db)
                )
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        fun prepareSyncDBWorker(): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            return PeriodicWorkRequest.Builder(SyncDatabaseWorker::class.java, 7, TimeUnit.DAYS)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()
        }
    }
}
