package com.iceteaviet.fastfoodfinder.ui.base

import androidx.annotation.NonNull
import com.iceteaviet.fastfoodfinder.data.DataManager
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by tom on 2019-04-18.
 */
abstract class BasePresenter<T>(protected val dataManager: DataManager) : Presenter {

    @NonNull
    protected var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun unsubscribe() {
        compositeDisposable.clear()
    }
}