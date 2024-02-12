package com.iceteaviet.fastfoodfinder.ui.ar


import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.Matrix
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.databinding.ActivityArCameraBinding
import com.iceteaviet.fastfoodfinder.location.LatLngAlt
import com.iceteaviet.fastfoodfinder.location.SystemLocationManager
import com.iceteaviet.fastfoodfinder.ui.ar.model.AugmentedPOI
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.ui.custom.ar.ARCamera
import com.iceteaviet.fastfoodfinder.ui.custom.ar.AROverlayView
import com.iceteaviet.fastfoodfinder.utils.REQUEST_CAMERA
import com.iceteaviet.fastfoodfinder.utils.REQUEST_LOCATION
import com.iceteaviet.fastfoodfinder.utils.extension.getSensorManager
import com.iceteaviet.fastfoodfinder.utils.formatDecimal
import com.iceteaviet.fastfoodfinder.utils.isCameraPermissionGranted
import com.iceteaviet.fastfoodfinder.utils.isLocationPermissionGranted
import com.iceteaviet.fastfoodfinder.utils.requestCameraPermission
import com.iceteaviet.fastfoodfinder.utils.requestLocationPermission

class LiveSightActivity : BaseActivity(), LiveSightContract.View, SensorEventListener {

    override lateinit var presenter: LiveSightContract.Presenter

    /**
     * Views Ref
     */
    private lateinit var binding: ActivityArCameraBinding

    private var surfaceView: SurfaceView? = null
    private var cameraContainerLayout: FrameLayout? = null
    private var arOverlayView: AROverlayView? = null
    private var camera: Camera? = null
    private var arCamera: ARCamera? = null
    private var sensorManager: SensorManager? = null

    override val layoutId: Int
        get() = R.layout.activity_ar_camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = LiveSightPresenter(App.getDataManager(), App.getSchedulerProvider(),
            SystemLocationManager.getInstance(), this)

        binding = ActivityArCameraBinding.inflate(layoutInflater)
        cameraContainerLayout = binding.cameraContainerLayout
        surfaceView = binding.surfaceView
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
        arOverlayView?.destroy()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
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

            arCamera?.let {
                projectionMatrix = it.projectionMatrix
            }

            Matrix.multiplyMM(rotatedProjectionMatrix, 0, projectionMatrix, 0, rotationMatrixFromVector, 0)
            arOverlayView?.updateRotatedProjectionMatrix(rotatedProjectionMatrix)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        //do nothing
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

    override fun showCannotGetLocationMessage() {
        Toast.makeText(this, R.string.cannot_get_curr_location, Toast.LENGTH_SHORT).show()
    }

    override fun showGeneralErrorMessage() {
        Toast.makeText(this, R.string.error_general_error_code, Toast.LENGTH_LONG).show()
    }

    override fun initAROverlayView() {
        if (arOverlayView?.parent != null) {
            (arOverlayView?.parent as ViewGroup).removeView(arOverlayView)
        }
        cameraContainerLayout?.addView(arOverlayView)
    }

    override fun initARCameraView() {
        reloadSurfaceView()

        if (arCamera == null) {
            arCamera = ARCamera(this, surfaceView!!)
        }
        if (arCamera?.parent != null) {
            (arCamera?.parent as ViewGroup).removeView(arCamera)
        }
        cameraContainerLayout?.addView(arCamera)
        arCamera?.keepScreenOn = true
        initCamera()
    }

    override fun addARPoint(arPoi: AugmentedPOI) {
        arOverlayView?.addArPoint(arPoi)
    }

    override fun setARPoints(arPoints: List<AugmentedPOI>) {
        arOverlayView?.setArPoints(arPoints)
    }

    private fun initCamera() {
        val numCams = Camera.getNumberOfCameras()
        if (numCams > 0) {
            try {
                camera = Camera.open()
                camera?.let {
                    it.startPreview()
                    arCamera?.setCamera(it)
                }
            } catch (ex: RuntimeException) {
                Toast.makeText(this, R.string.camera_not_found, Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun reloadSurfaceView() {
        if (surfaceView?.parent != null) {
            (surfaceView?.parent as ViewGroup).removeView(surfaceView)
        }

        cameraContainerLayout?.addView(surfaceView)
    }

    override fun releaseARCamera() {
        arCamera?.setCamera(null)
        camera?.let {
            it.setPreviewCallback(null)
            it.stopPreview()
            it.release()
        }
        camera = null
    }

    override fun initSensorService() {
        if (sensorManager == null)
            sensorManager = this.getSensorManager()

        registerSensorListeners()
    }

    private fun registerSensorListeners() {
        sensorManager?.registerListener(this,
            sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
            SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun updateLatestLocation(latestLocation: LatLngAlt) {
        arOverlayView?.let {
            it.updateCurrentLocation(latestLocation)
            binding.tvCurrentLocation.text = String.format("lat: %s \nlon: %s \nalt: %s \n",
                formatDecimal(latestLocation.latitude, 4),
                formatDecimal(latestLocation.longitude, 4),
                formatDecimal(latestLocation.altitude, 4))
        }
    }

    companion object {
        private val TAG = LiveSightActivity::class.java.simpleName
    }
}