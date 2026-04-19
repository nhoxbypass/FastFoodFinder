package com.iceteaviet.fastfoodfinder.utils

import android.content.Context
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.domain.model.Store

object StoreSyncHelper {
    suspend fun refreshStoresFromRemote(context: Context, clientAuth: ClientAuth, storeRepository: StoreRepository): List<Store> {
        if (clientAuth.isSignedIn()) {
            return storeRepository.refreshStores()
        } else {
            try {
                clientAuth.signInWithEmailAndPassword(
                    context.getString(R.string.downloader_bot_email),
                    context.getString(R.string.downloader_bot_pwd)
                )
                return storeRepository.refreshStores()
            } finally {
                clientAuth.signOut()
            }
        }
    }
}
