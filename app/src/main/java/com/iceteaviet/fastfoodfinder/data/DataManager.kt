package com.iceteaviet.fastfoodfinder.data

import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.prefs.PreferencesRepository
import com.iceteaviet.fastfoodfinder.data.domain.routing.MapsRoutingRepository
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import io.reactivex.Single

/**
 * Created by tom on 7/9/18.
 *
 * Central data access point.
 * We may not need this if we use few repository. But it's important when we have many repositories
 */
interface DataManager : ClientAuth, StoreRepository, UserRepository, MapsRoutingRepository, PreferencesRepository {
    fun loadStoresFromServer(): Single<List<Store>>

    fun getCurrentUser(): User?

    fun updateCurrentUser(user: User?)
}
