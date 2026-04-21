package com.iceteaviet.fastfoodfinder.ui.main.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.core.location.LatLngAlt
import com.iceteaviet.fastfoodfinder.core.location.LocationListener
import com.iceteaviet.fastfoodfinder.core.location.base.ILocationManager
import com.iceteaviet.fastfoodfinder.data.domain.prefs.PreferencesRepository
import com.iceteaviet.fastfoodfinder.data.domain.routing.MapsRoutingRepository
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.data.remote.routing.GoogleMapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.domain.model.Store
import com.iceteaviet.fastfoodfinder.domain.model.toLatLng
import com.iceteaviet.fastfoodfinder.ui.main.search.SearchEventBus
import com.iceteaviet.fastfoodfinder.ui.main.search.SearchEventResult
import com.iceteaviet.fastfoodfinder.ui.main.map.model.MapCameraPosition
import com.iceteaviet.fastfoodfinder.ui.main.map.model.NearByStore
import com.iceteaviet.fastfoodfinder.utils.Constant
import com.iceteaviet.fastfoodfinder.utils.distanceBetween
import com.iceteaviet.fastfoodfinder.utils.getLatLngString
import com.iceteaviet.fastfoodfinder.utils.isValidLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MainMapEvent {
    object RequestLocationPermission : MainMapEvent()
    data class SetMyLocationEnabled(val enabled: Boolean) : MainMapEvent()
    data class AnimateMapCamera(val location: LatLng, val zoomToDetail: Boolean) : MainMapEvent()
    data class ShowWarningMessage(val stringId: Int) : MainMapEvent()
    object ShowGeneralErrorMessage : MainMapEvent()
    object ShowCannotGetLocationMessage : MainMapEvent()
    object ShowInvalidStoreLocationWarning : MainMapEvent()
    data class AddStoresToCluster(val stores: List<Store>) : MainMapEvent()
    object SetupMap : MainMapEvent()
    data class ShowMapRoutingView(val store: Store, val mapsDirection: MapsDirection) : MainMapEvent()
    data class ShowDialogStoreInfo(val store: Store) : MainMapEvent()
    data class SetNearByStores(val stores: List<NearByStore>) : MainMapEvent()
    object ClearNearByStores : MainMapEvent()
}

@OptIn(FlowPreview::class)
@HiltViewModel
class MainMapViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val mapsRoutingRepository: MapsRoutingRepository,
    private val searchEventBus: SearchEventBus,
    private val preferencesRepository: PreferencesRepository
) : ViewModel(), LocationListener {

    private lateinit var locationManager: ILocationManager

    fun injectLocationManager(mgr: ILocationManager) {
        this.locationManager = mgr
    }

    private val _uiEvent = MutableSharedFlow<MainMapEvent>(extraBufferCapacity = 128)
    val uiEvent: SharedFlow<MainMapEvent> = _uiEvent.asSharedFlow()

    private var currLocation: LatLng? = null
    private var storeList: List<Store> = ArrayList()

    private var isZoomToUser = false
    private var locationGranted = false
    private var isStoresLoaded = false

    private var locationTimeoutJob: Job? = null

    private val cameraPositionFlow = MutableSharedFlow<MapCameraPosition>(extraBufferCapacity = 64)

    init {
        setupFlows()
    }

    private fun setupFlows() {
        cameraPositionFlow
            .debounce(200)
            .map { position ->
                val visible = getVisibleStore(storeList, position.cameraBounds)
                generateNearByStoresWithDistance(position.cameraPosition, visible)
            }
            .flowOn(kotlinx.coroutines.Dispatchers.Default)
            .onEach { nearbyStores ->
                _uiEvent.tryEmit(MainMapEvent.SetNearByStores(nearbyStores))
            }
            .launchIn(viewModelScope)
    }

    private var isBusRegistered = false

    fun start(hasLocationPermission: Boolean) {
        resetState()

        if (!hasLocationPermission) {
            _uiEvent.tryEmit(MainMapEvent.RequestLocationPermission)
        } else {
            onLocationPermissionGranted()
        }

        if (!isBusRegistered) {
            searchEventBus.events
                .onEach { onSearchResult(it) }
                .launchIn(viewModelScope)
            isBusRegistered = true
        }

        _uiEvent.tryEmit(MainMapEvent.SetupMap)

        if (!isStoresLoaded) {
            loadAllStoresToMap()
        }

        if (!isZoomToUser) {
            startLocationTimeout()
        }
    }

    override fun onCleared() {
        locationTimeoutJob?.cancel()
        if (::locationManager.isInitialized) {
            unsubscribeLocationUpdate()
        }
        super.onCleared()
    }

    fun onLocationPermissionGranted() {
        if (::locationManager.isInitialized) {
            subscribeLocationUpdate()
            requestCurrentLocation()
        }
        _uiEvent.tryEmit(MainMapEvent.SetMyLocationEnabled(true))
        locationGranted = true
    }

    fun requestCurrentLocation() {
        if (!::locationManager.isInitialized) return
        val lastLocation = locationManager.getCurrentLocation()
        if (lastLocation != null) {
            onCurrLocationChanged(lastLocation.latitude, lastLocation.longitude)
        }
    }

    override fun onLocationChanged(location: LatLngAlt) {
        onCurrLocationChanged(location.latitude, location.longitude)
    }

    override fun onLocationFailed(type: Int) {
        locationTimeoutJob?.cancel()
        if (!isZoomToUser) {
            fallbackToLastKnownOrDefault()
        }
    }

    fun onMapCameraMove(cameraPosition: LatLng, bounds: LatLngBounds) {
        cameraPositionFlow.tryEmit(MapCameraPosition(cameraPosition, bounds))
    }

    fun onCameraMoveStarted() {
        _uiEvent.tryEmit(MainMapEvent.ClearNearByStores)
    }

    fun onGetMapAsync() {
        if (storeList.isNotEmpty()) {
            _uiEvent.tryEmit(MainMapEvent.AddStoresToCluster(storeList))
        }

        if (locationGranted) {
            _uiEvent.tryEmit(MainMapEvent.SetMyLocationEnabled(true))
            requestCurrentLocation()
        }
    }

    fun onNavigationButtonClick(store: Store) {
        val storeLocation = store.toLatLng()
        val queries = HashMap<String, String>()

        if (!isValidLocation(storeLocation)) {
            _uiEvent.tryEmit(MainMapEvent.ShowInvalidStoreLocationWarning)
            return
        }

        if (!isValidLocation(currLocation)) {
            _uiEvent.tryEmit(MainMapEvent.ShowCannotGetLocationMessage)
            return
        }

        val origin = getLatLngString(currLocation)
        val destination = getLatLngString(storeLocation)

        queries[GoogleMapsRoutingApiHelper.PARAM_ORIGIN] = origin
        queries[GoogleMapsRoutingApiHelper.PARAM_DESTINATION] = destination

        viewModelScope.launch {
            try {
                val mapsDirection = mapsRoutingRepository.getMapsDirection(queries, store)
                if (mapsDirection.routeList.isNotEmpty()) {
                    _uiEvent.tryEmit(MainMapEvent.ShowMapRoutingView(store, mapsDirection))
                } else {
                    _uiEvent.tryEmit(MainMapEvent.ShowGeneralErrorMessage)
                }
            } catch (e: Exception) {
                _uiEvent.tryEmit(MainMapEvent.ShowGeneralErrorMessage)
            }
        }
    }

    fun onNearByStoreClicked(store: Store) {
        _uiEvent.tryEmit(MainMapEvent.AnimateMapCamera(store.toLatLng(), true))
        _uiEvent.tryEmit(MainMapEvent.ShowDialogStoreInfo(store))
    }

    private fun onSearchResult(searchEventResult: SearchEventResult) {
        when (searchEventResult.resultCode) {
            SearchEventResult.SEARCH_ACTION_QUICK -> {
                handleSearchQuickAction(searchEventResult.storeType)
            }
            SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT -> {
                if (searchEventResult.searchString.isBlank()) return
                handleSearchQuerySubmitAction(searchEventResult.searchString)
            }
            SearchEventResult.SEARCH_ACTION_COLLAPSE -> {
                handleSearchCollapseAction()
            }
            SearchEventResult.SEARCH_ACTION_STORE_CLICK -> {
                searchEventResult.store?.let {
                    handleSearchStoreClickAction(it)
                }
            }
            else -> _uiEvent.tryEmit(MainMapEvent.ShowGeneralErrorMessage)
        }
    }

    private fun resetState() {
        locationGranted = false
    }

    private fun subscribeLocationUpdate() {
        locationManager.requestLocationUpdates()
        locationManager.subscribeLocationUpdate(this)
    }

    private fun unsubscribeLocationUpdate() {
        locationManager.unsubscribeLocationUpdate(this)
    }

    private fun onCurrLocationChanged(latitude: Double, longitude: Double) {
        currLocation = LatLng(latitude, longitude)
        preferencesRepository.setLastKnownLocation(latitude, longitude)

        if (!isZoomToUser) {
            locationTimeoutJob?.cancel()
            _uiEvent.tryEmit(MainMapEvent.AnimateMapCamera(currLocation!!, false))
            isZoomToUser = true
        }
    }

    private fun startLocationTimeout() {
        locationTimeoutJob?.cancel()
        locationTimeoutJob = viewModelScope.launch {
            delay(LOCATION_TIMEOUT_MS)
            if (!isZoomToUser) {
                fallbackToLastKnownOrDefault()
            }
        }
    }

    private fun fallbackToLastKnownOrDefault() {
        isZoomToUser = true
        val persisted = preferencesRepository.getLastKnownLocation()
        val target = if (persisted != null) {
            LatLng(persisted.first, persisted.second)
        } else {
            Constant.DEFAULT_MAP_TARGET
        }
        _uiEvent.tryEmit(MainMapEvent.AnimateMapCamera(target, false))
    }

    private fun getVisibleStore(storeList: List<Store>, bounds: LatLngBounds): List<Store> {
        val stores = ArrayList<Store>()
        for (i in storeList.indices) {
            val store = storeList[i]
            if (bounds.contains(store.toLatLng())) {
                stores.add(store)
            }
        }
        return stores
    }

    private fun generateNearByStoresWithDistance(currPos: LatLng, stores: List<Store>): List<NearByStore> {
        return stores.map { store ->
            NearByStore(store, distanceBetween(currPos, store.toLatLng()))
        }.sortedBy { it.distance }
    }

    private fun handleSearchQuickAction(storeType: Int) {
        viewModelScope.launch {
            try {
                val foundStores = storeRepository.findStoresByType(storeType)
                if (foundStores.isEmpty()) {
                    _uiEvent.tryEmit(MainMapEvent.ShowWarningMessage(R.string.store_not_found))
                } else {
                    storeList = foundStores
                    _uiEvent.tryEmit(MainMapEvent.AddStoresToCluster(storeList))
                    _uiEvent.tryEmit(MainMapEvent.AnimateMapCamera(storeList[0].toLatLng(), false))
                }
            } catch (e: Exception) {
                _uiEvent.tryEmit(MainMapEvent.ShowWarningMessage(R.string.get_store_data_failed))
            }
        }
    }

    private fun handleSearchQuerySubmitAction(searchString: String) {
        viewModelScope.launch {
            try {
                val foundStores = storeRepository.findStores(searchString)
                if (foundStores.isEmpty()) {
                    _uiEvent.tryEmit(MainMapEvent.ShowWarningMessage(R.string.store_not_found))
                } else {
                    storeList = foundStores
                    _uiEvent.tryEmit(MainMapEvent.AddStoresToCluster(storeList))
                    _uiEvent.tryEmit(MainMapEvent.AnimateMapCamera(storeList[0].toLatLng(), false))
                }
            } catch (e: Exception) {
                _uiEvent.tryEmit(MainMapEvent.ShowWarningMessage(R.string.get_store_data_failed))
            }
        }
    }

    private fun handleSearchCollapseAction() {
        loadAllStoresToMap()
        currLocation?.let {
            _uiEvent.tryEmit(MainMapEvent.AnimateMapCamera(it, false))
        }
    }

    private fun loadAllStoresToMap() {
        viewModelScope.launch {
            try {
                val allStores = storeRepository.getAllStores()
                storeList = allStores
                isStoresLoaded = true
                if (storeList.isEmpty()) {
                    _uiEvent.tryEmit(MainMapEvent.ShowWarningMessage(R.string.get_store_data_failed))
                } else {
                    _uiEvent.tryEmit(MainMapEvent.AddStoresToCluster(storeList))
                }
            } catch (e: Exception) {
                _uiEvent.tryEmit(MainMapEvent.ShowWarningMessage(R.string.get_store_data_failed))
            }
        }
    }

    private fun handleSearchStoreClickAction(store: Store) {
        storeList = arrayListOf(store)
        _uiEvent.tryEmit(MainMapEvent.AddStoresToCluster(storeList))
        _uiEvent.tryEmit(MainMapEvent.AnimateMapCamera(store.toLatLng(), false))
        _uiEvent.tryEmit(MainMapEvent.ClearNearByStores)
        _uiEvent.tryEmit(MainMapEvent.ShowDialogStoreInfo(store))
    }

    companion object {
        private const val LOCATION_TIMEOUT_MS = 4000L
    }
}
