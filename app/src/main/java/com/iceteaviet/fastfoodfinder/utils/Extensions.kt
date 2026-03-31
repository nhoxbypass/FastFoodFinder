package com.iceteaviet.fastfoodfinder.utils
import android.content.Context
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.utils.isValidUserUid
import io.reactivex.Single




fun loadStoresFromServerHelper(context: Context, clientAuth: ClientAuth, storeRepository: StoreRepository): Single<List<Store>> {
    return if (clientAuth.isSignedIn()) {
        storeRepository.getAllStores()
    } else {
        clientAuth.signInWithEmailAndPassword("anonymous@fastfoodfinder.com", "123456")
            .ignoreElement()
            .andThen(storeRepository.getAllStores())
    }
}


fun getCurrentUserHelper(clientAuth: ClientAuth, userRepository: UserRepository): User? {
    val uid = clientAuth.getCurrentUserUid()
    if (isValidUserUid(uid)) {
        try {
            return userRepository.getUser(uid).blockingGet()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return null
}
