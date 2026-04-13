package com.iceteaviet.fastfoodfinder.utils

import android.content.Context
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User

suspend fun loadStoresFromServerHelper(context: Context, clientAuth: ClientAuth, storeRepository: StoreRepository): List<Store> {
    return if (clientAuth.isSignedIn()) {
        storeRepository.getAllStores()
    } else {
        clientAuth.signInWithEmailAndPassword("anonymous@fastfoodfinder.com", "123456")
        storeRepository.getAllStores()
    }
}

suspend fun getCurrentUserHelper(clientAuth: ClientAuth, userRepository: UserRepository): User? {
    val uid = clientAuth.getCurrentUserUid()
    if (isValidUserUid(uid)) {
        return try {
            userRepository.getUser(uid)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    return null
}
