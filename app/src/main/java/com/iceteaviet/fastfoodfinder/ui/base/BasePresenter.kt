package com.iceteaviet.fastfoodfinder.ui.base

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by tom on 2019-04-18.
 */
abstract class BasePresenter<T>(protected val dataManager: DataManager,
                                protected var schedulerProvider: SchedulerProvider) : Presenter {

    protected val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun unsubscribe() {
        compositeDisposable.clear()
    }
}