package com.iceteaviet.fastfoodfinder.data

import com.google.firebase.auth.AuthCredential
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreDataSource
import com.iceteaviet.fastfoodfinder.data.domain.user.UserDataSource
import com.iceteaviet.fastfoodfinder.data.prefs.PreferencesHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.MapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import io.reactivex.Single

/**
 * Created by tom on 2019-05-26.
 */

class FakeDataManager(private val localStoreDataSource: StoreDataSource, private val remoteStoreDataSource: StoreDataSource,
                      private val clientAuth: ClientAuth,
                      private val localUserDataSource: UserDataSource, private val remoteUserDataSource: UserDataSource,
                      private val mapsRoutingApiHelper: MapsRoutingApiHelper, private val preferencesHelper: PreferencesHelper) : DataManager {
    override fun getLocalStoreDataSource(): StoreDataSource {
        return localStoreDataSource
    }

    override fun getRemoteStoreDataSource(): StoreDataSource {
        return remoteStoreDataSource
    }

    override fun getLocalUserDataSource(): UserDataSource {
        return localUserDataSource
    }

    override fun getRemoteUserDataSource(): UserDataSource {
        return remoteUserDataSource
    }

    override fun getMapsRoutingApiHelper(): MapsRoutingApiHelper {
        return mapsRoutingApiHelper
    }

    override fun getPreferencesHelper(): PreferencesHelper {
        return preferencesHelper
    }

    override fun signUpWithEmailAndPassword(email: String, password: String): Single<User> {
        return clientAuth.signUpWithEmailAndPassword(email, password)
    }

    override fun isSignedIn(): Boolean {
        return clientAuth.isSignedIn()
    }

    override fun signOut() {
        clientAuth.signOut()
        setCurrentUser(null)
    }

    override fun signInWithEmailAndPassword(email: String, password: String): Single<User> {
        return clientAuth.signInWithEmailAndPassword(email, password)
    }

    override fun signInWithCredential(authCredential: AuthCredential): Single<User> {
        return clientAuth.signInWithCredential(authCredential)
    }

    override fun loadStoresFromServer(): Single<List<Store>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCurrentUser(): User? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCurrentUser(user: User?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSearchHistories(): MutableSet<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addSearchHistories(searchContent: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCurrentUserUid(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}