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
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by tom on 2019-05-26.
 */

class FakeDataManager(context: Context, private val storeRepository: StoreRepository, private val userRepository: UserRepository,
                      private val clientAuth: ClientAuth,
                      private val mapsRoutingRepository: MapsRoutingRepository, private val preferencesRepository: PreferencesRepository) : DataManager {

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

    override fun loadStoresFromServer(): Single<List<Store>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCurrentUser(): User? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateCurrentUser(user: User?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCurrentUserUid(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}