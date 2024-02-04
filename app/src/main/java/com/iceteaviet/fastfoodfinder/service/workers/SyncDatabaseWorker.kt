package com.iceteaviet.fastfoodfinder.service.workers

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.filterInvalidData
import com.iceteaviet.fastfoodfinder.utils.ui.NotiManager
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit


/**
 * Created by tom on 2019-07-07.
 *
 * Note that RxWorker.createWork() is called on the main thread, but the return value is subscribed on a background thread by default
 */
class SyncDatabaseWorker(ctx: Context, params: WorkerParameters) : RxWorker(ctx, params) {

    private val dataManager: DataManager
    private val notiManager: NotiManager

    init {
        // TODO: Inject these dependencies
        dataManager = App.getDataManager()
        notiManager = App.getNotiManager()
    }

    override fun createWork(): Single<Result> {
        return Single.create { emitter ->
            notiManager.showStoreSyncProgressStatusNotification(applicationContext.getString(R.string.str_updating_store_db), applicationContext.getString(R.string.str_update_app_db))

            dataManager.loadStoresFromServer()
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onSuccess(storeList: List<Store>) {
                        val filteredStoreList = filterInvalidData(storeList.toMutableList())
                        dataManager.setStores(filteredStoreList)

                        if (!filteredStoreList.isEmpty()) {
                            notiManager.showStoreSyncStatusNotification(
                                String.format(applicationContext.getString(R.string.update_database_successfull_with_count), filteredStoreList.size),
                                applicationContext.getString(R.string.str_update_app_db))
                            emitter.onSuccess(Result.success())
                        } else {
                            emitter.onSuccess(Result.retry())
                        }
                    }

                    override fun onError(e: Throwable) {
                        emitter.onSuccess(Result.failure())
                    }
                })
        }
    }

    companion object {
        fun prepareSyncDBWorker(): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val myWorkBuilder = PeriodicWorkRequest.Builder(SyncDatabaseWorker::class.java, 7, TimeUnit.DAYS)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.DAYS) // Backoff retry after 1 day
                .setConstraints(constraints)

            return myWorkBuilder.build()
        }
    }
}