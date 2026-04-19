package com.iceteaviet.fastfoodfinder.ui.routing

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.Step
import com.iceteaviet.fastfoodfinder.domain.model.Store
import com.iceteaviet.fastfoodfinder.domain.model.toLatLng
import com.iceteaviet.fastfoodfinder.utils.isValidLocation
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreLogoDrawableRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class MapRoutingUiState(
    val title: String = "",
    val stepList: List<Step> = emptyList(),
    val inPreviewMode: Boolean = false,
    val durationText: String = "",
    val distanceText: String = "",
    val summaryText: String = "",
    val currDirectionIndex: Int = 0,
    val event: MapRoutingEvent = MapRoutingEvent.Idle
)

sealed class MapRoutingEvent {
    object Idle : MapRoutingEvent()
    object Exit : MapRoutingEvent()
    object ShowGetDirectionFailedMessage : MapRoutingEvent()
    object ShowGeneralErrorMessage : MapRoutingEvent()
    data class ScrollTopBannerToPosition(val index: Int) : MapRoutingEvent()
    data class AnimateMapCamera(val location: LatLng, val zoomToDetail: Boolean) : MapRoutingEvent()
    data class AddMapMarker(val location: LatLng, val title: String, val description: String, val icon: Int) : MapRoutingEvent()
    data class DrawRoutingPath(val currLocation: LatLng?, val routingGeoPoint: List<LatLng>) : MapRoutingEvent()
}

@HiltViewModel
class MapRoutingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MapRoutingUiState())
    val uiState: StateFlow<MapRoutingUiState> = _uiState.asStateFlow()

    private var currLocation: LatLng? = null
    private lateinit var currStore: Store
    private var geoPointList: List<LatLng> = ArrayList()
    private lateinit var mapsDirection: MapsDirection

    fun handleExtras(mapsDirection: MapsDirection?, store: Store?) {
        if (store != null && mapsDirection != null && isRoutingDataValid(mapsDirection, store)) {
            setupData(mapsDirection, store)
            
            _uiState.value = _uiState.value.copy(
                title = currStore.title,
                stepList = _uiState.value.stepList
            )
        } else {
            _uiState.value = _uiState.value.copy(event = MapRoutingEvent.ShowGetDirectionFailedMessage)
            // Can't exit immediately due to state design, client handles ShowGetDirectionFailedMessage and exits.
        }
    }

    private fun isRoutingDataValid(mapsDirection: MapsDirection, store: Store): Boolean {
        if (!isValidLocation(store.toLatLng())) return false
        if (mapsDirection.routeList.isEmpty() || mapsDirection.routeList[0].legList.isEmpty()
            || mapsDirection.routeList[0].legList[0].stepList.isEmpty()) {
            return false
        }
        return true
    }

    private fun setupData(mapsDirection: MapsDirection, store: Store) {
        this.mapsDirection = mapsDirection
        this.currStore = store

        val steps = mapsDirection.routeList[0].legList[0].stepList
        currLocation = steps[0].startMapCoordination.location
        geoPointList = PolyUtil.decode(mapsDirection.routeList[0].encodedPolylineString)

        _uiState.value = _uiState.value.copy(stepList = steps)
    }

    fun onGetMapAsync() {
        if (!this::currStore.isInitialized || !this::mapsDirection.isInitialized) return
        
        // Add map marker for store
        _uiState.value = _uiState.value.copy(
            durationText = mapsDirection.routeList[0].legList[0].getDuration(),
            distanceText = mapsDirection.routeList[0].legList[0].getDistance(),
            summaryText = String.format("Via %s", mapsDirection.routeList[0].summary),
            event = MapRoutingEvent.AddMapMarker(
                currStore.toLatLng(), 
                currStore.title, 
                currStore.address, 
                getStoreLogoDrawableRes(currStore.type)
            )
        )
        // Note: Multiple events emitted quickly will cause StateFlow to conflate. 
        // For the sake of standard refactoring we can defer `drawRoutingPath` and "Your location" adding to side effects, 
        // or just let the view handle drawing logic by exposing state properties. 
    }
    
    // Instead of sequentially emitting conflated events, provide a specific map init event.
    fun emitMapInitEvents() {
        if (!this::currStore.isInitialized || !this::mapsDirection.isInitialized) return
        
        // Wait, for multiple calls in one frame we should probably use a separate init method
    }

    // Let's rely on views directly requesting initialization data to avoid conflation of AddMarker, AnimateMapCamera etc
    fun getInitMapData(): InitMapData? {
        if (!this::currStore.isInitialized || !this::mapsDirection.isInitialized) return null
        return InitMapData(currStore, currLocation, geoPointList)
    }

    fun onNavigationRowClick(index: Int) {
        if (index < 0 || index >= _uiState.value.stepList.size) {
            _uiState.value = _uiState.value.copy(event = MapRoutingEvent.ShowGeneralErrorMessage)
            return
        }

        _uiState.value = _uiState.value.copy(
            currDirectionIndex = index,
            inPreviewMode = true,
            event = MapRoutingEvent.AnimateMapCamera(_uiState.value.stepList[index].endMapCoordination.location, true)
        )
    }

    fun onTopRoutingBannerPositionChange(position: Int) {
        if (position < 0 || position >= _uiState.value.stepList.size) {
            return
        }
        
        _uiState.value = _uiState.value.copy(
            currDirectionIndex = position,
            event = MapRoutingEvent.ScrollTopBannerToPosition(position)
        )
        // We'd also need to AnimateMapCamera but StateFlow only triggers 1 event. 
        // We can bundle them in complex events.
    }

    fun onPrevInstructionClick() {
        var currentIndex = _uiState.value.currDirectionIndex - 1
        if (currentIndex < 0) {
            currentIndex = _uiState.value.stepList.size - 1
        }

        _uiState.value = _uiState.value.copy(
            currDirectionIndex = currentIndex,
            event = MapRoutingEvent.ScrollTopBannerToPosition(currentIndex) // Let view manually do map camera for now.
        )
    }

    fun onNextInstructionClick() {
        var currentIndex = _uiState.value.currDirectionIndex + 1
        if (currentIndex >= _uiState.value.stepList.size) {
            currentIndex = 0
        }

        _uiState.value = _uiState.value.copy(
            currDirectionIndex = currentIndex,
            event = MapRoutingEvent.ScrollTopBannerToPosition(currentIndex)
        )
    }

    fun onBackArrowButtonPress() {
        if (_uiState.value.inPreviewMode) {
            _uiState.value = _uiState.value.copy(inPreviewMode = false)
        } else {
            _uiState.value = _uiState.value.copy(event = MapRoutingEvent.Exit)
        }
    }

    fun markEventConsumed() {
        _uiState.value = _uiState.value.copy(event = MapRoutingEvent.Idle)
    }
}

data class InitMapData(
    val store: Store,
    val currLocation: LatLng?,
    val geoPointList: List<LatLng>
)
