package com.iceteaviet.fastfoodfinder.data.domain.store

import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.domain.model.Store
import javax.inject.Inject

class StoreRefreshService @Inject constructor(
    private val storeRepository: StoreRepository,
    private val clientAuth: ClientAuth,
    private val botEmail: String,
    private val botPassword: String,
) {
    suspend fun refreshStoresFromRemote(): List<Store> {
        if (clientAuth.isSignedIn()) {
            return storeRepository.refreshStores()
        }

        clientAuth.signInWithEmailAndPassword(botEmail, botPassword)
        try {
            return storeRepository.refreshStores()
        } finally {
            clientAuth.signOut()
        }
    }
}
