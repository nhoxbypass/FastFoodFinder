package com.iceteaviet.fastfoodfinder.ui.main.map

import androidx.collection.SparseArrayCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.core.location.LatLngAlt
import com.iceteaviet.fastfoodfinder.core.location.LocationListener
import com.iceteaviet.fastfoodfinder.core.location.base.ILocationManager
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MainMapEvent {
    object Idle : MainMapEvent()
    object RequestLocationPermission : MainMapEvent()
    data class SetMyLocationEnabled(val enabled: Boolean) : MainMapEvent()
    data class AnimateMapCamera(val location: LatLng, val zoomToDetail: Boolean) : MainMapEvent()
    data class ShowWarningMessage(val stringId: Int) : MainMapEvent()
    object ShowGeneralErrorMessage : MainMapEvent()
    object ShowCannotGetLocationMessage : MainMapEvent()
    object ShowInvalidStoreLocationWarning : MainMapEvent()
    data class AddMarkersToMap(val stores: List<Store>) : MainMapEvent()
    object SetupMap : MainMapEvent()
    object SetupMapEventHandlers : MainMapEvent()
    data class ShowMapRoutingView(val store: Store, val mapsDirection: MapsDirection) : MainMapEvent()
    data class ShowDialogStoreInfo(val store: Store) : MainMapEvent()
    data class AnimateMapMarker(val storeId: Int, val storeType: Int) : MainMapEvent()
    data class SetNearByStores(val stores: List<NearByStore>) : MainMapEvent()
    object ClearNearByStores : MainMapEvent()
    object ClearMapData : MainMapEvent()
}

@OptIn(FlowPreview::class)
@HiltViewModel
class MainMapViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val mapsRoutingRepository: MapsRoutingRepository,
    private val searchEventBus: SearchEventBus
) : ViewModel(), LocationListener {

    private lateinit var locationManager: ILocationManager

    fun injectLocationManager(mgr: ILocationManager) {
        this.locationManager = mgr
    }

    private val _uiState = MutableStateFlow(MainMapEvent.Idle as MainMapEvent)
    val uiState: StateFlow<MainMapEvent> = _uiState.asStateFlow()

    private var currLocation: LatLng? = null
    private var storeList: List<Store> = ArrayList()
    private var visibleStores: List<Store> = ArrayList()

    private var isZoomToUser = false
    private var locationGranted = false
    private var isStoresLoaded = false

    private val cameraPositionFlow = MutableSharedFlow<MapCameraPosition>(extraBufferCapacity = 64)
    private val newVisibleStoreFlow = MutableSharedFlow<Store>(extraBufferCapacity = 64)

    init {
        setupFlows()
    }

    private fun setupFlows() {
        cameraPositionFlow
            .debounce(200)
            .map { position ->
                val stores = getVisibleStore(storeList, position.cameraBounds)
                stores.forEach { store ->
                    if (!visibleStores.contains(store)) {
                        newVisibleStoreFlow.tryEmit(store)
                    }
                }
                visibleStores = stores
                generateNearByStoresWithDistance(position.cameraPosition, stores)
            }
            .onEach { nearbyStores ->
                _uiState.value = MainMapEvent.SetNearByStores(nearbyStores)
            }
            .launchIn(viewModelScope)

        newVisibleStoreFlow
            .onEach { store ->
                _uiState.value = MainMapEvent.AnimateMapMarker(store.id, store.type)
            }
            .launchIn(viewModelScope)
    }

    private var isBusRegistered = false

    fun start(hasLocationPermission: Boolean) {
        resetState()

        if (!hasLocationPermission) {
            _uiState.value = MainMapEvent.RequestLocationPermission
        } else {
            onLocationPermissionGranted()
        }

        if (!isBusRegistered) {
            searchEventBus.events
                .onEach { onSearchResult(it) }
                .launchIn(viewModelScope)
            isBusRegistered = true
        }

        _uiState.value = MainMapEvent.SetupMap

        if (!isStoresLoaded) {
            loadAllStoresToMap()
        }
    }

    override fun onCleared() {
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
        _uiState.value = MainMapEvent.SetMyLocationEnabled(true)
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
    }

    fun onMapCameraMove(cameraPosition: LatLng, bounds: LatLngBounds) {
        cameraPositionFlow.tryEmit(MapCameraPosition(cameraPosition, bounds))
    }

    fun onGetMapAsync() {
        if (storeList.isNotEmpty()) {
            _uiState.value = MainMapEvent.AddMarkersToMap(storeList)
        }

        if (locationGranted) {
            _uiState.value = MainMapEvent.SetMyLocationEnabled(true)
            requestCurrentLocation()
        }

        _uiState.value = MainMapEvent.SetupMapEventHandlers
    }

    fun onNavigationButtonClick(store: Store) {
        val storeLocation = store.toLatLng()
        val queries = HashMap<String, String>()

        if (!isValidLocation(storeLocation)) {
            _uiState.value = MainMapEvent.ShowInvalidStoreLocationWarning
            return
        }

        if (!isValidLocation(currLocation)) {
            _uiState.value = MainMapEvent.ShowCannotGetLocationMessage
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
                    _uiState.value = MainMapEvent.ShowMapRoutingView(store, mapsDirection)
                } else {
                    _uiState.value = MainMapEvent.ShowGeneralErrorMessage
                }
            } catch (e: Exception) {
                _uiState.value = MainMapEvent.ShowGeneralErrorMessage
            }
        }
    }

    fun onClearOldMapData() {
        _uiState.value = MainMapEvent.ClearMapData
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
            else -> _uiState.value = MainMapEvent.ShowGeneralErrorMessage
        }
    }

    private fun resetState() {
        locationGranted = false
        isZoomToUser = false
    }

    private fun subscribeLocationUpdate() {
        locationManager.requestLocationUpdates()
        locationManager.subscribeLocationUpdate(this)
    }

    private fun unsubscribeLocationUpdate() {
        locationManager.unsubscribeLocationUpdate(this)
    }

    private fun onCurrLocationChanged(latitude: Double, longitude: Double) {
        currLocation = LatLng(latitude, longitude).also {
            if (!isZoomToUser) {
                _uiState.value = MainMapEvent.AnimateMapCamera(it, false)
                isZoomToUser = true
            }
        }
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
        val res = ArrayList<NearByStore>()
        for (store in stores) {
            res.add(NearByStore(store, distanceBetween(currPos, store.toLatLng())))
        }
        return res
    }

    private fun handleSearchQuickAction(storeType: Int) {
        viewModelScope.launch {
            try {
                val foundStores = storeRepository.findStoresByType(storeType)
                if (foundStores.isEmpty()) {
                    _uiState.value = MainMapEvent.ShowWarningMessage(R.string.store_not_found)
                } else {
                    storeList = foundStores
                    _uiState.value = MainMapEvent.AddMarkersToMap(storeList)
                    _uiState.value = MainMapEvent.AnimateMapCamera(storeList[0].toLatLng(), false)
                }
            } catch (e: Exception) {
                _uiState.value = MainMapEvent.ShowWarningMessage(R.string.get_store_data_failed)
            }
        }
    }

    private fun handleSearchQuerySubmitAction(searchString: String) {
        viewModelScope.launch {
            try {
                val foundStores = storeRepository.findStores(searchString)
                if (foundStores.isEmpty()) {
                    _uiState.value = MainMapEvent.ShowWarningMessage(R.string.store_not_found)
                } else {
                    storeList = foundStores
                    _uiState.value = MainMapEvent.AddMarkersToMap(storeList)
                    _uiState.value = MainMapEvent.AnimateMapCamera(storeList[0].toLatLng(), false)
                }
            } catch (e: Exception) {
                _uiState.value = MainMapEvent.ShowWarningMessage(R.string.get_store_data_failed)
            }
        }
    }

    private fun handleSearchCollapseAction() {
        loadAllStoresToMap()
        currLocation?.let {
            _uiState.value = MainMapEvent.AnimateMapCamera(it, false)
        }
    }

    private fun loadAllStoresToMap() {
        viewModelScope.launch {
            try {
                val allStores = storeRepository.getAllStores()
                storeList = allStores
                isStoresLoaded = true
                if (storeList.isEmpty()) {
                    _uiState.value = MainMapEvent.ShowWarningMessage(R.string.get_store_data_failed)
                } else {
                    _uiState.value = MainMapEvent.AddMarkersToMap(storeList)
                }
            } catch (e: Exception) {
                _uiState.value = MainMapEvent.ShowWarningMessage(R.string.get_store_data_failed)
            }
        }
    }

    private fun handleSearchStoreClickAction(store: Store) {
        storeList = arrayListOf(store)
        _uiState.value = MainMapEvent.AddMarkersToMap(storeList)
        _uiState.value = MainMapEvent.AnimateMapCamera(store.toLatLng(), false)
        _uiState.value = MainMapEvent.ClearNearByStores
        _uiState.value = MainMapEvent.ShowDialogStoreInfo(store)
    }

    fun markEventConsumed() {
        _uiState.value = MainMapEvent.Idle
    }
}
