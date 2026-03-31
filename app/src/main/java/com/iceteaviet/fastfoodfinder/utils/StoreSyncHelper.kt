package com.iceteaviet.fastfoodfinder.utils

import android.content.Context
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

object StoreSyncHelper {
    fun loadStoresFromServer(context: Context, clientAuth: ClientAuth, storeRepository: StoreRepository): Single<List<Store>> {
        if (clientAuth.isSignedIn()) {
            return storeRepository.getAllStores()
        } else {
            return Single.create { emitter ->
                clientAuth.signInWithEmailAndPassword(context.getString(R.string.downloader_bot_email), context.getString(R.string.downloader_bot_pwd))
                    .ignoreElement()
                    .andThen(storeRepository.getAllStores())
                    .subscribe(object : SingleObserver<List<Store>> {
                        override fun onSubscribe(d: Disposable) {
                            emitter.setDisposable(d)
                        }

                        override fun onSuccess(storeList: List<Store>) {
                            clientAuth.signOut()
                            emitter.onSuccess(storeList)
                        }

                        override fun onError(e: Throwable) {
                            clientAuth.signOut()
                            emitter.onError(e)
                        }
                    })
            }
        }
    }
}
