package com.iceteaviet.fastfoodfinder.ui.ar


import android.content.Context
import android.content.pm.PackageManager
import android.hardware.*
import android.location.Location
import android.opengl.Matrix
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
import com.iceteaviet.fastfoodfinder.ui.ar.model.AugmentedPOI
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.ui.custom.ar.ARCamera
import com.iceteaviet.fastfoodfinder.ui.custom.ar.AROverlayView
import com.iceteaviet.fastfoodfinder.utils.*
import com.iceteaviet.fastfoodfinder.utils.location.LocationListener
import com.iceteaviet.fastfoodfinder.utils.location.LocationManager
import kotlinx.android.synthetic.main.activity_ar_camera.*

class LiveSightActivity : BaseActivity(), LiveSightContract.View, SensorEventListener, LocationListener {

    override lateinit var presenter: LiveSightContract.Presenter

    private var surfaceView: SurfaceView? = null
    private var cameraContainerLayout: FrameLayout? = null
    private var arOverlayView: AROverlayView? = null
    private var camera: Camera? = null
    private var arCamera: ARCamera? = null
    private var tvCurrentLocation: TextView? = null
    private var sensorManager: SensorManager? = null

    override val layoutId: Int
        get() = R.layout.activity_ar_camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = LiveSightPresenter(App.getDataManager(), this)

        cameraContainerLayout = camera_container_layout
        surfaceView = surface_view
        tvCurrentLocation = tv_current_location
        arOverlayView = AROverlayView(this)
    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        presenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
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
                    presenter.onLocationPermissionGranted()
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
            }

            REQUEST_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.onCameraPermissionGranted()
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
        presenter.onLocationChanged(location)
    }

    override fun onLocationFailed(type: Int) {

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

    override fun requestLocationPermission() {
        requestLocationPermission(this)
    }

    override fun requestCameraPermission() {
        requestCameraPermission(this)
    }

    override fun isLocationPermissionGranted(): Boolean {
        return isLocationPermissionGranted(this)
    }

    override fun isCameraPermissionGranted(): Boolean {
        return isCameraPermissionGranted(this)
    }


    override fun initAROverlayView() {
        if (arOverlayView!!.parent != null) {
            (arOverlayView!!.parent as ViewGroup).removeView(arOverlayView)
        }
        cameraContainerLayout!!.addView(arOverlayView)
    }

    override fun initARCameraView() {
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

    override fun addARPoint(arPoi: AugmentedPOI) {
        arOverlayView!!.addArPoint(arPoi)
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

    override fun releaseARCamera() {
        if (camera != null) {
            camera!!.setPreviewCallback(null)
            camera!!.stopPreview()
            arCamera!!.setCamera(null)
            camera!!.release()
            camera = null
        }
    }

    override fun initSensorService() {
        if (sensorManager == null)
            sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        registerSensorListeners()
    }

    private fun registerSensorListeners() {
        sensorManager!!.registerListener(this,
                sensorManager!!.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun subscribeLocationServices() {
        LocationManager.getInstance().addListener(this)
        presenter.onLocationChanged(LocationManager.getInstance().getCurrentLocation())
    }

    override fun unsubscribeLocationServices() {
        LocationManager.getInstance().removeListener(this)
    }

    override fun updateLatestLocation(latestLocation: Location) {
        if (arOverlayView != null) {
            arOverlayView!!.updateCurrentLocation(latestLocation)
            tvCurrentLocation!!.text = String.format("lat: %s \nlon: %s \nalt: %s \n",
                    formatDecimal(latestLocation.latitude, 4),
                    formatDecimal(latestLocation.longitude, 4),
                    formatDecimal(latestLocation.altitude, 4))
        }
    }

    companion object {
        private val TAG = LiveSightActivity::class.java.simpleName
    }
}
