package com.iceteaviet.fastfoodfinder.ui.store

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.iceteaviet.fastfoodfinder.core.location.LatLngAlt
import com.iceteaviet.fastfoodfinder.core.location.LocationListener
import com.iceteaviet.fastfoodfinder.core.location.base.ILocationManager
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.routing.MapsRoutingRepository
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository
import com.iceteaviet.fastfoodfinder.data.remote.routing.GoogleMapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.store.StoreDetailActivity.Companion.KEY_STORE
import com.iceteaviet.fastfoodfinder.utils.getCurrentUserHelper
import com.iceteaviet.fastfoodfinder.utils.getLatLngString
import com.iceteaviet.fastfoodfinder.utils.isValidLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StoreDetailUiState(
    val title: String = "",
    val isSignedIn: Boolean = false,
    val comments: List<Comment> = emptyList(),
    val event: StoreDetailEvent = StoreDetailEvent.Idle
)

sealed class StoreDetailEvent {
    object Idle : StoreDetailEvent()
    object RequestLocationPermission : StoreDetailEvent()
    object ShowCannotGetLocationMessage : StoreDetailEvent()
    data class AddStoreComment(val comment: Comment) : StoreDetailEvent()
    object ScrollToCommentList : StoreDetailEvent()
    object ShowCommentEditorView : StoreDetailEvent()
    data class StartCallIntent(val tel: String) : StoreDetailEvent()
    object ShowInvalidPhoneNumbWarning : StoreDetailEvent()
    data class ShowMapRoutingView(val store: Store, val mapsDirection: MapsDirection) : StoreDetailEvent()
    object Exit : StoreDetailEvent()
    object ShowStoreAddedToFavMessage : StoreDetailEvent()
    object ShowGeneralErrorMessage : StoreDetailEvent()
    object ShowInvalidStoreLocationWarning : StoreDetailEvent()
    object ShowLoginRequestToast : StoreDetailEvent()
}

@HiltViewModel
class StoreDetailViewModel @Inject constructor(
    private val clientAuth: ClientAuth,
    private val userRepository: UserRepository,
    private val storeRepository: StoreRepository,
    private val mapsRoutingRepository: MapsRoutingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(), LocationListener {

    private val _uiState = MutableStateFlow(StoreDetailUiState())
    val uiState: StateFlow<StoreDetailUiState> = _uiState.asStateFlow()

    private var locationManager: ILocationManager? = null
    private var currLocation: LatLng? = null

    val currStore: Store? = savedStateHandle.get<Store>(KEY_STORE)

    init {
        if (currStore == null) {
            _uiState.value = _uiState.value.copy(event = StoreDetailEvent.Exit)
        } else {
            _uiState.value = _uiState.value.copy(title = currStore.title)
        }
    }

    fun injectLocationManager(mgr: ILocationManager) {
        this.locationManager = mgr
    }

    fun start(hasLocationPermission: Boolean) {
        if (currStore == null) return

        viewModelScope.launch {
            val currUser = getCurrentUserHelper(clientAuth, userRepository)
            _uiState.value = _uiState.value.copy(isSignedIn = currUser != null)
            
            try {
                val commentList = storeRepository.getComments(currStore.id.toString())
                _uiState.value = _uiState.value.copy(comments = commentList.reversed())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (!hasLocationPermission) {
            _uiState.value = _uiState.value.copy(event = StoreDetailEvent.RequestLocationPermission)
        } else {
            onLocationPermissionGranted()
        }
    }

    override fun onCleared() {
        unsubscribeLocationUpdate()
        super.onCleared()
    }

    fun onLocationPermissionGranted() {
        requestCurrentLocation()
        subscribeLocationUpdate()
    }

    private fun requestCurrentLocation() {
        val mgr = locationManager ?: return
        val lastLocation = mgr.getCurrentLocation()
        if (lastLocation != null) {
            currLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
        } else {
            _uiState.value = _uiState.value.copy(event = StoreDetailEvent.ShowCannotGetLocationMessage)
        }
    }

    override fun onLocationChanged(location: LatLngAlt) {
        currLocation = LatLng(location.latitude, location.longitude)
    }

    override fun onLocationFailed(type: Int) {}

    fun onAddNewComment(comment: Comment?) {
        comment?.let {
            _uiState.value = _uiState.value.copy(event = StoreDetailEvent.AddStoreComment(it))
            if (currStore != null) {
                viewModelScope.launch {
                    storeRepository.insertOrUpdateComment(currStore.id.toString(), it)
                }
            }
        }
    }

    fun onCommentButtonClick() {
        viewModelScope.launch {
            val currUser = getCurrentUserHelper(clientAuth, userRepository)
            if (currUser != null) {
                _uiState.value = _uiState.value.copy(event = StoreDetailEvent.ShowCommentEditorView)
            } else {
                _uiState.value = _uiState.value.copy(event = StoreDetailEvent.ShowLoginRequestToast)
            }
        }
    }

    fun onCallButtonClick() {
        if (currStore != null) {
            if (currStore.tel.isNotEmpty()) {
                _uiState.value = _uiState.value.copy(event = StoreDetailEvent.StartCallIntent(currStore.tel))
            } else {
                _uiState.value = _uiState.value.copy(event = StoreDetailEvent.ShowInvalidPhoneNumbWarning)
            }
        }
    }

    fun onNavigationButtonClick() {
        if (currStore == null) return

        val storeLocation = currStore.getPosition()
        val queries = HashMap<String, String>()

        if (!isValidLocation(storeLocation)) {
            _uiState.value = _uiState.value.copy(event = StoreDetailEvent.ShowInvalidStoreLocationWarning)
            return
        }

        if (!isValidLocation(currLocation)) {
            _uiState.value = _uiState.value.copy(event = StoreDetailEvent.ShowCannotGetLocationMessage)
            return
        }

        val origin = getLatLngString(currLocation)
        val destination = getLatLngString(storeLocation)

        queries[GoogleMapsRoutingApiHelper.PARAM_ORIGIN] = origin
        queries[GoogleMapsRoutingApiHelper.PARAM_DESTINATION] = destination

        viewModelScope.launch {
            try {
                val mapsDirection = mapsRoutingRepository.getMapsDirection(queries, currStore)
                if (mapsDirection.routeList.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(event = StoreDetailEvent.ShowMapRoutingView(currStore, mapsDirection))
                } else {
                    _uiState.value = _uiState.value.copy(event = StoreDetailEvent.ShowGeneralErrorMessage)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(event = StoreDetailEvent.ShowGeneralErrorMessage)
            }
        }
    }

    fun onAddToFavButtonClick() {
        viewModelScope.launch {
            val currUser = getCurrentUserHelper(clientAuth, userRepository)
            if (currUser == null) {
                _uiState.value = _uiState.value.copy(event = StoreDetailEvent.ShowLoginRequestToast)
            }
        }
    }

    fun onSaveButtonClick() {
        viewModelScope.launch {
            val currUser = getCurrentUserHelper(clientAuth, userRepository)
            if (currUser == null) {
                _uiState.value = _uiState.value.copy(event = StoreDetailEvent.ShowLoginRequestToast)
            }
        }
    }

    fun onBackButtonClick() {
        _uiState.value = _uiState.value.copy(event = StoreDetailEvent.Exit)
    }

    private fun subscribeLocationUpdate() {
        locationManager?.requestLocationUpdates()
        locationManager?.subscribeLocationUpdate(this)
    }

    private fun unsubscribeLocationUpdate() {
        locationManager?.unsubscribeLocationUpdate(this)
    }

    fun markEventConsumed() {
        _uiState.value = _uiState.value.copy(event = StoreDetailEvent.Idle)
    }

    fun clearAddCommentEventConsumed() {
        _uiState.value = _uiState.value.copy(event = StoreDetailEvent.ScrollToCommentList)
    }
}
