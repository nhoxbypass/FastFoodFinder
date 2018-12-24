package com.iceteaviet.fastfoodfinder.ui.ar

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.*
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.opengl.Matrix
import android.os.Build
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.local.poi.model.AugmentedPOI
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.ui.custom.ar.ARCamera
import com.iceteaviet.fastfoodfinder.ui.custom.ar.AROverlayView
import com.iceteaviet.fastfoodfinder.utils.*
import com.iceteaviet.fastfoodfinder.utils.Constant.DEFAULT_MAP_TARGET
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreLogoDrawableRes
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_ar_camera.*

class ArCameraActivity : BaseActivity(), SensorEventListener, LocationListener {

    private var location: Location = Location(LocationManager.PASSIVE_PROVIDER)
    private var surfaceView: SurfaceView? = null
    private var cameraContainerLayout: FrameLayout? = null
    private var arOverlayView: AROverlayView? = null
    private var camera: Camera? = null
    private var arCamera: ARCamera? = null
    private var tvCurrentLocation: TextView? = null
    private var sensorManager: SensorManager? = null
    private lateinit var dataManager: DataManager

    override val layoutId: Int
        get() = R.layout.activity_ar_camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        cameraContainerLayout = camera_container_layout
        surfaceView = surface_view
        tvCurrentLocation = tv_current_location
        arOverlayView = AROverlayView(this)

        dataManager = App.getDataManager()
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isLocationPermissionGranted(this)) {
            requestLocationPermission(this)
        } else {
            initLocationService()
        }
        checkCameraPermission()
        registerSensors()
        initAROverlayView()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    override fun onPause() {
        releaseCamera()
        super.onPause()
    }

    override fun onDestroy() {
        arOverlayView!!.destroy()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initLocationService()
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
            }

            REQUEST_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initARCameraView()
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
            }

            else -> {
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrixFromVector = FloatArray(16)
            var projectionMatrix = FloatArray(16)
            val rotatedProjectionMatrix = FloatArray(16)

            SensorManager.getRotationMatrixFromVector(rotationMatrixFromVector, event.values)

            if (arCamera != null) {
                projectionMatrix = arCamera!!.projectionMatrix
            }

            Matrix.multiplyMM(rotatedProjectionMatrix, 0, projectionMatrix, 0, rotationMatrixFromVector, 0)
            this.arOverlayView!!.updateRotatedProjectionMatrix(rotatedProjectionMatrix)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        //do nothing
    }

    override fun onLocationChanged(location: Location) {
        dataManager.getLocalStoreDataSource().getStoreInBounds(location.latitude - RADIUS,
                location.longitude - RADIUS,
                location.latitude + RADIUS,
                location.longitude + RADIUS)
                .observeOn(Schedulers.computation())
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(storeList: List<Store>) {
                        for (i in storeList.indices) {
                            arOverlayView!!.addArPoint(AugmentedPOI(storeList[i].title!!,
                                    java.lang.Double.valueOf(storeList[i].lat)!!,
                                    java.lang.Double.valueOf(storeList[i].lng)!!,
                                    0.0,
                                    getStoreLogoDrawableRes(storeList[i].type)))
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        d(TAG, "Provider: $provider. Status: $status")
    }

    override fun onProviderEnabled(provider: String) {
        d(TAG, "onProviderEnabled$provider")
    }

    override fun onProviderDisabled(provider: String) {
        d(TAG, "onProviderDisabled$provider")
    }

    fun checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isCameraPermissionGranted(this)) {
            requestCameraPermission(this)
        } else {
            initARCameraView()
        }
    }


    fun initAROverlayView() {
        if (arOverlayView!!.parent != null) {
            (arOverlayView!!.parent as ViewGroup).removeView(arOverlayView)
        }
        cameraContainerLayout!!.addView(arOverlayView)
    }

    fun initARCameraView() {
        reloadSurfaceView()

        if (arCamera == null) {
            arCamera = ARCamera(this, surfaceView!!)
        }
        if (arCamera!!.parent != null) {
            (arCamera!!.parent as ViewGroup).removeView(arCamera)
        }
        cameraContainerLayout!!.addView(arCamera)
        arCamera!!.keepScreenOn = true
        initCamera()
    }

    private fun initCamera() {
        val numCams = Camera.getNumberOfCameras()
        if (numCams > 0) {
            try {
                camera = Camera.open()
                camera!!.startPreview()
                arCamera!!.setCamera(camera!!)
            } catch (ex: RuntimeException) {
                Toast.makeText(this, R.string.camera_not_found, Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun reloadSurfaceView() {
        if (surfaceView!!.parent != null) {
            (surfaceView!!.parent as ViewGroup).removeView(surfaceView)
        }

        cameraContainerLayout!!.addView(surfaceView)
    }

    private fun releaseCamera() {
        if (camera != null) {
            camera!!.setPreviewCallback(null)
            camera!!.stopPreview()
            arCamera!!.setCamera(null)
            camera!!.release()
            camera = null
        }
    }

    private fun registerSensors() {
        sensorManager!!.registerListener(this,
                sensorManager!!.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_FASTEST)
    }

    @SuppressLint("MissingPermission")
    private fun initLocationService() {
        try {
            val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (locationManager == null) {
                Toast.makeText(this, R.string.cannot_connect_location_service, Toast.LENGTH_SHORT).show()
                return
            }

            // Get GPS and network status
            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                var loc: Location? = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (loc == null) {
                    loc = Location(LocationManager.PASSIVE_PROVIDER)
                    loc.latitude = DEFAULT_MAP_TARGET.latitude
                    loc.longitude = DEFAULT_MAP_TARGET.longitude
                }
                location = loc
                updateLatestLocation()
            }

            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)

                var loc: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (loc == null) {
                    loc = Location(LocationManager.PASSIVE_PROVIDER)
                    loc.latitude = DEFAULT_MAP_TARGET.latitude
                    loc.longitude = DEFAULT_MAP_TARGET.longitude
                }
                location = loc
                updateLatestLocation()
            }
        } catch (ex: Exception) {
            e(ex, TAG)

        }

    }

    private fun updateLatestLocation() {
        if (arOverlayView != null) {
            arOverlayView!!.updateCurrentLocation(location)
            tvCurrentLocation!!.text = String.format("lat: %s \nlon: %s \nalt: %s \n",
                    formatDecimal(location.latitude, 4),
                    formatDecimal(location.longitude, 4),
                    formatDecimal(location.altitude, 4))
        }
    }

    companion object {

        private val TAG = ArCameraActivity::class.java.simpleName
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters
        private const val MIN_TIME_BW_UPDATES = (1000 * 30).toLong() // 30 seconds
        private const val RADIUS = 0.005
    }
}
