package com.iceteaviet.fastfoodfinder.utils
import android.content.Context
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


fun loadStoresFromServerHelper(context: Context, clientAuth: ClientAuth, storeRepository: StoreRepository): Single<List<Store>> {
    return if (clientAuth.isSignedIn()) {
        storeRepository.getAllStores().subscribeOn(Schedulers.io())
    } else {
        clientAuth.signInWithEmailAndPassword("anonymous@fastfoodfinder.com", "123456")
            .observeOn(io.reactivex.schedulers.Schedulers.io())
            .ignoreElement()
            .andThen(storeRepository.getAllStores().subscribeOn(Schedulers.io()))
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
