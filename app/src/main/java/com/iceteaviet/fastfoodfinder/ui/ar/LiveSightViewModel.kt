package com.iceteaviet.fastfoodfinder.ui.ar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iceteaviet.fastfoodfinder.core.location.LatLngAlt
import com.iceteaviet.fastfoodfinder.core.location.LocationListener
import com.iceteaviet.fastfoodfinder.core.location.SystemLocationManager
import com.iceteaviet.fastfoodfinder.core.location.base.ILocationManager
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.ui.ar.model.AugmentedPOI
import com.iceteaviet.fastfoodfinder.utils.isLolipopOrHigher
import com.iceteaviet.fastfoodfinder.utils.storesToArPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

data class LiveSightUiState(
    val latestLocation: LatLngAlt? = null,
    val arPoints: List<AugmentedPOI> = emptyList(),
    val event: LiveSightEvent = LiveSightEvent.Idle
)

sealed class LiveSightEvent {
    object Idle : LiveSightEvent()
    object CheckPermissionsAndInit : LiveSightEvent()
    object InitARCameraView : LiveSightEvent()
    object RequestLocationPermission : LiveSightEvent()
    object RequestCameraPermission : LiveSightEvent()
    object ShowCannotGetLocationMessage : LiveSightEvent()
    object ShowGeneralErrorMessage : LiveSightEvent()
}

@HiltViewModel
class LiveSightViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel(), LocationListener {

    private val _uiState = MutableStateFlow(LiveSightUiState())
    val uiState: StateFlow<LiveSightUiState> = _uiState.asStateFlow()

    // Guard against stacking simultaneous DB queries when location fires rapidly
    @Volatile
    private var isLoadingArPoints = false

    // ILocationManager is a singleton managed outside Hilt - access directly
    private val locationManager: ILocationManager
        get() = SystemLocationManager.getInstance()

    fun start() {
        _uiState.value = _uiState.value.copy(event = LiveSightEvent.CheckPermissionsAndInit)
    }

    override fun onCleared() {
        unsubscribeLocationUpdate()
        super.onCleared()
    }

    fun handlePermissions(locationGranted: Boolean, cameraGranted: Boolean) {
        if (isLolipopOrHigher() && !locationGranted) {
            _uiState.value = _uiState.value.copy(event = LiveSightEvent.RequestLocationPermission)
        } else {
            onLocationPermissionGranted()
        }

        if (isLolipopOrHigher() && !cameraGranted) {
            _uiState.value = _uiState.value.copy(event = LiveSightEvent.RequestCameraPermission)
        } else {
            _uiState.value = _uiState.value.copy(event = LiveSightEvent.InitARCameraView)
        }
    }

    fun onLocationPermissionGranted() {
        subscribeLocationUpdate()
        requestCurrentLocation()
    }

    fun onCameraPermissionGranted() {
        _uiState.value = _uiState.value.copy(event = LiveSightEvent.InitARCameraView)
    }

    private fun requestCurrentLocation() {
        val lastLocation = locationManager.getCurrentLocation()
        if (lastLocation != null) {
            onLocationChanged(lastLocation)
        } else {
            _uiState.value = _uiState.value.copy(event = LiveSightEvent.ShowCannotGetLocationMessage)
        }
    }

    override fun onLocationChanged(location: LatLngAlt) {
        _uiState.value = _uiState.value.copy(latestLocation = location)

        // Drop update if a query is already in-flight to prevent stacked Realm queries
        if (isLoadingArPoints) return

        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            isLoadingArPoints = true
            try {
                val storeList = storeRepository.getStoreInBounds(location.latitude, location.longitude, RADIUS).await()
                launch(kotlinx.coroutines.Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(arPoints = storesToArPoints(storeList))
                }
            } catch (e: Exception) {
                launch(kotlinx.coroutines.Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(event = LiveSightEvent.ShowGeneralErrorMessage)
                }
            } finally {
                isLoadingArPoints = false
            }
        }
    }

    override fun onLocationFailed(type: Int) {
    }

    private fun subscribeLocationUpdate() {
        // Always unsubscribe first to prevent duplicate registrations across onResume cycles
        locationManager.unsubscribeLocationUpdate(this)
        locationManager.requestLocationUpdates()
        locationManager.subscribeLocationUpdate(this)
    }

    fun unsubscribeLocationUpdate() {
        locationManager.unsubscribeLocationUpdate(this)
    }

    fun markEventConsumed() {
        _uiState.value = _uiState.value.copy(event = LiveSightEvent.Idle)
    }

    companion object {
        const val RADIUS = 0.01
    }
}
