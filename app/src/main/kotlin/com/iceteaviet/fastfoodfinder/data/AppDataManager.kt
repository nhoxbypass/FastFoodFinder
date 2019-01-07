package com.iceteaviet.fastfoodfinder.data

import android.app.Activity
import android.content.Context
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreDataSource
import com.iceteaviet.fastfoodfinder.data.domain.user.UserDataSource
import com.iceteaviet.fastfoodfinder.data.prefs.PreferencesHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.MapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.utils.Constant
import com.iceteaviet.fastfoodfinder.utils.isEmpty
import com.iceteaviet.fastfoodfinder.utils.isValidUserUid
import com.iceteaviet.fastfoodfinder.utils.w
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by tom on 7/9/18.
 */

class AppDataManager(context: Context, private val localStoreDataSource: StoreDataSource, private val remoteStoreDataSource: StoreDataSource,
                     private val clientAuth: ClientAuth,
                     private val localUserDataSource: UserDataSource, private val remoteUserDataSource: UserDataSource,
                     private val mapsRoutingApiHelper: MapsRoutingApiHelper, private val preferencesHelper: PreferencesHelper) : DataManager {

    private var currentUser: User? = null
    private lateinit var searchHistory: MutableSet<String>

    init {

        Realm.init(context)
        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(config)
    }

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

    override fun loadStoresFromServer(activity: Activity): Single<List<Store>> {
        return Single.create { emitter ->
            if (!clientAuth.isSignedIn()) {
                // Not signed in
                clientAuth.signInWithEmailAndPassword(Constant.DOWNLOADER_BOT_EMAIL, Constant.DOWNLOADER_BOT_PWD)
                        .subscribe(object : SingleObserver<Boolean> {
                            override fun onSubscribe(d: Disposable) {

                            }

                            override fun onSuccess(b: Boolean) {
                                if (b) {
                                    remoteStoreDataSource.getAllStores()
                                            .subscribe(object : SingleObserver<List<Store>> {
                                                override fun onSubscribe(d: Disposable) {

                                                }

                                                override fun onSuccess(storeList: List<Store>) {
                                                    signOut()
                                                    emitter.onSuccess(storeList)
                                                }

                                                override fun onError(e: Throwable) {
                                                    emitter.onError(e)
                                                }
                                            })
                                } else {
                                    // Do nothing
                                    w(TAG, "Sign In failed ")
                                }
                            }

                            override fun onError(e: Throwable) {
                                w(TAG, "Sign In failed " + e.message)
                                emitter.onError(e)
                            }
                        })
            } else {
                remoteStoreDataSource.getAllStores()
                        .subscribe(object : SingleObserver<List<Store>> {
                            override fun onSubscribe(d: Disposable) {

                            }

                            override fun onSuccess(storeList: List<Store>) {
                                emitter.onSuccess(storeList)
                            }

                            override fun onError(e: Throwable) {
                                emitter.onError(e)
                            }
                        })
            }
        }
    }

    override fun getCurrentUserUid(): String {
        var uid = ""
        if (currentUser != null)
            uid = currentUser!!.uid

        if (isEmpty(uid))
            uid = clientAuth.getCurrentUserUid()

        if (isEmpty(uid))
            uid = preferencesHelper.getCurrentUserUid()

        return uid
    }

    override fun isSignedIn(): Boolean {
        return clientAuth.isSignedIn()
    }

    override fun signOut() {
        clientAuth.signOut()
        setCurrentUser(null)
    }

    override fun signInWithEmailAndPassword(email: String, password: String): Single<Boolean> {
        return clientAuth.signInWithEmailAndPassword(email, password)
    }

    override fun signInWithCredential(authCredential: AuthCredential): Single<FirebaseUser> {
        return clientAuth.signInWithCredential(authCredential)
    }

    override fun getCurrentUser(): User? {
        if (currentUser == null) {
            val uid = getCurrentUserUid()
            if (isValidUserUid(uid)) {
                try {
                    currentUser = localUserDataSource.getUser(uid)
                            .blockingGet()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return currentUser
    }

    override fun setCurrentUser(user: User?) {
        currentUser = user
        if (user != null && !user.uid.isEmpty()) {
            preferencesHelper.setCurrentUserUid(user.uid)
            localUserDataSource.insertOrUpdate(user)
        } else {
            preferencesHelper.setCurrentUserUid("")
        }
    }

    override fun getSearchHistories(): MutableSet<String> {
        if (!::searchHistory.isInitialized)
            searchHistory = preferencesHelper.getSearchHistories()

        return searchHistory
    }

    override fun addSearchHistories(searchContent: String) {
        if (!::searchHistory.isInitialized)
            searchHistory = preferencesHelper.getSearchHistories()

        searchHistory.add(searchContent)
        preferencesHelper.setSearchHistories(searchHistory)
    }

    companion object {
        private val TAG = AppDataManager::class.java.simpleName
    }
}
