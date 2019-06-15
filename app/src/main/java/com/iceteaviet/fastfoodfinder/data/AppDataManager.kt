package com.iceteaviet.fastfoodfinder.data

import android.content.Context
import androidx.core.util.Pair
import com.google.firebase.auth.AuthCredential
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.prefs.PreferencesRepository
import com.iceteaviet.fastfoodfinder.data.domain.routing.MapsRoutingRepository
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.utils.Constant
import com.iceteaviet.fastfoodfinder.utils.isEmpty
import com.iceteaviet.fastfoodfinder.utils.isValidUserUid
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by tom on 7/9/18.
 */

class AppDataManager(context: Context, private val storeRepository: StoreRepository, private val userRepository: UserRepository,
                     private val clientAuth: ClientAuth,
                     private val mapsRoutingRepository: MapsRoutingRepository, private val preferencesRepository: PreferencesRepository) : DataManager {

    private var currentUser: User? = null

    init {
        Realm.init(context)
        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(config)
    }

    override fun loadStoresFromServer(): Single<List<Store>> {
        if (clientAuth.isSignedIn()) {
            return storeRepository.getAllStores()
        } else {
            return Single.create { emitter ->
                // Not signed in
                clientAuth.signInWithEmailAndPassword(Constant.DOWNLOADER_BOT_EMAIL, Constant.DOWNLOADER_BOT_PWD)
                        .toCompletable()
                        .andThen(storeRepository.getAllStores())
                        .subscribe(object : SingleObserver<List<Store>> {
                            override fun onSubscribe(d: Disposable) {
                                emitter.setDisposable(d)
                            }

                            override fun onSuccess(storeList: List<Store>) {
                                signOut()
                                emitter.onSuccess(storeList)
                            }

                            override fun onError(e: Throwable) {
                                signOut()
                                emitter.onError(e)
                            }
                        })
            }
        }
    }

    override fun getCurrentUserUid(): String {
        var uid = ""

        currentUser?.let {
            uid = it.getUid()
        }

        if (isEmpty(uid))
            uid = clientAuth.getCurrentUserUid()

        return uid
    }

    override fun signUpWithEmailAndPassword(email: String, password: String): Single<User> {
        return clientAuth.signUpWithEmailAndPassword(email, password)
    }

    override fun isSignedIn(): Boolean {
        return clientAuth.isSignedIn()
    }

    override fun signOut() {
        clientAuth.signOut()
        updateCurrentUser(null)
    }

    override fun signInWithEmailAndPassword(email: String, password: String): Single<User> {
        return clientAuth.signInWithEmailAndPassword(email, password)
    }

    override fun signInWithCredential(authCredential: AuthCredential): Single<User> {
        return clientAuth.signInWithCredential(authCredential)
    }

    override fun getCurrentUser(): User? {
        if (currentUser == null) {
            val uid = getCurrentUserUid()
            if (isValidUserUid(uid)) {
                try {
                    currentUser = userRepository.getUser(uid)
                            .blockingGet()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return currentUser
    }

    override fun updateCurrentUser(user: User?) {
        currentUser = user
        if (user != null && !user.getUid().isEmpty()) {
            userRepository.insertOrUpdateUser(user)
        }
    }

    override fun getAllStores(): Single<List<Store>> {
        return storeRepository.getAllStores()
    }

    override fun setStores(storeList: List<Store>) {
        storeRepository.setStores(storeList)
    }

    override fun getStoreInBounds(minLat: Double, minLng: Double, maxLat: Double, maxLng: Double): Single<List<Store>> {
        return storeRepository.getStoreInBounds(minLat, minLng, maxLat, maxLng)
    }

    override fun findStores(queryString: String): Single<List<Store>> {
        return storeRepository.findStores(queryString)
    }

    override fun findStoresByCustomAddress(customQuerySearch: List<String>): Single<List<Store>> {
        return storeRepository.findStoresByCustomAddress(customQuerySearch)
    }

    override fun findStoresBy(key: String, value: Int): Single<List<Store>> {
        return storeRepository.findStoresBy(key, value)
    }

    override fun findStoresBy(key: String, values: List<Int>): Single<List<Store>> {
        return storeRepository.findStoresBy(key, values)
    }

    override fun findStoresByType(type: Int): Single<List<Store>> {
        return storeRepository.findStoresByType(type)
    }

    override fun findStoreById(id: Int): Single<Store> {
        return storeRepository.findStoreById(id)
    }

    override fun findStoresByIds(ids: List<Int>): Single<List<Store>> {
        return storeRepository.findStoresByIds(ids)
    }

    override fun deleteAllStores() {
        return storeRepository.deleteAllStores()
    }

    override fun getComments(storeId: String): Single<List<Comment>> {
        return storeRepository.getComments(storeId)
    }

    override fun insertOrUpdateComment(storeId: String, comment: Comment) {
        return storeRepository.insertOrUpdateComment(storeId, comment)
    }

    override fun insertOrUpdateUser(name: String, email: String, photoUrl: String, uid: String, storeLists: List<UserStoreList>) {
        return userRepository.insertOrUpdateUser(name, email, photoUrl, uid, storeLists)
    }

    override fun insertOrUpdateUser(user: User) {
        return userRepository.insertOrUpdateUser(user)
    }

    override fun updateStoreListForUser(uid: String, storeLists: List<UserStoreList>) {
        return userRepository.updateStoreListForUser(uid, storeLists)
    }

    override fun getUser(uid: String): Single<User> {
        return userRepository.getUser(uid)
    }

    override fun isUserExists(uid: String): Single<Boolean> {
        return userRepository.isUserExists(uid)
    }

    override fun subscribeFavouriteStoresOfUser(uid: String): Observable<Pair<Int, Int>> {
        return userRepository.subscribeFavouriteStoresOfUser(uid)
    }

    override fun unsubscribeFavouriteStoresOfUser(uid: String) {
        return userRepository.unsubscribeFavouriteStoresOfUser(uid)
    }

    override fun getMapsDirection(queries: Map<String, String>, store: Store): Single<MapsDirection> {
        return mapsRoutingRepository.getMapsDirection(queries, store)
    }

    override fun getAppLaunchFirstTime(): Boolean {
        return preferencesRepository.getAppLaunchFirstTime()
    }

    override fun setAppLaunchFirstTime(isFirstTime: Boolean) {
        preferencesRepository.setAppLaunchFirstTime(isFirstTime)
    }

    override fun setSearchHistories(set: MutableSet<String>) {
        preferencesRepository.setSearchHistories(set)
    }

    override fun getIfLanguageIsVietnamese(): Boolean {
        return preferencesRepository.getIfLanguageIsVietnamese()
    }

    override fun setIfLanguageIsVietnamese(isVietnamese: Boolean) {
        preferencesRepository.setIfLanguageIsVietnamese(isVietnamese)
    }

    override fun getSearchHistories(): MutableSet<String> {
        return preferencesRepository.getSearchHistories()
    }

    override fun addSearchHistories(searchContent: String) {
        preferencesRepository.addSearchHistories(searchContent)
    }

    companion object {
        private val TAG = AppDataManager::class.java.simpleName
    }
}
