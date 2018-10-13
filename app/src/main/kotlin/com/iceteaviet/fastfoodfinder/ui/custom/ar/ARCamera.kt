package com.iceteaviet.fastfoodfinder.ui.custom.ar

import android.app.Activity
import android.content.Context
import android.hardware.Camera
import android.opengl.Matrix
import android.view.*
import com.iceteaviet.fastfoodfinder.utils.e
import java.io.IOException

/**
 * Created by Genius Doan on 20/07/2017.
 */

class ARCamera(context: Context, surfaceView: SurfaceView) : ViewGroup(context), SurfaceHolder.Callback {
    private val TAG = ARCamera::class.java.simpleName

    private var previewSize: Camera.Size? = null
    private var supportedPreviewSizes: List<Camera.Size>? = null
    private var camera: Camera? = null
    private val activity: Activity
    val projectionMatrix = FloatArray(16)
    private var cameraWidth: Int = 0
    private var cameraHeight: Int = 0

    private val cameraOrientation: Int
        get() {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info)

            val rotation = activity.windowManager.defaultDisplay.rotation

            var degrees = 0
            when (rotation) {
                Surface.ROTATION_0 -> degrees = 0
                Surface.ROTATION_90 -> degrees = 90
                Surface.ROTATION_180 -> degrees = 180
                Surface.ROTATION_270 -> degrees = 270
                else -> degrees = 0
            }

            var orientation: Int
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                orientation = (info.orientation + degrees) % 360
                orientation = (360 - orientation) % 360
            } else {
                orientation = (info.orientation - degrees + 360) % 360
            }

            return orientation
        }

    init {
        this.activity = context as Activity
        val surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(this)
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = View.resolveSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = View.resolveSize(suggestedMinimumHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)

        if (supportedPreviewSizes != null) {
            previewSize = getOptimalPreviewSize(supportedPreviewSizes, width, height)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            if (camera != null) {
                val orientation = cameraOrientation

                camera!!.setDisplayOrientation(orientation)
                camera!!.parameters.setRotation(orientation)

                camera!!.setPreviewDisplay(holder)
            }
        } catch (exception: IOException) {
            e(exception, TAG)
        }

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (camera != null) {
            this.cameraWidth = width
            this.cameraHeight = height

            val params = camera!!.parameters
            params.setPreviewSize(previewSize!!.width, previewSize!!.height)
            requestLayout()

            camera!!.parameters = params
            camera!!.startPreview()

            generateProjectionMatrix()
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        if (camera != null) {
            camera!!.setPreviewCallback(null)
            camera!!.stopPreview()
            camera!!.release()
            camera = null
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (changed && childCount > 0) {
            val child = getChildAt(0)

            val width = r - l
            val height = b - t

            var previewWidth = width
            var previewHeight = height
            if (previewSize != null) {
                previewWidth = previewSize!!.width
                previewHeight = previewSize!!.height
            }

            if (width * previewHeight > height * previewWidth) {
                val scaledChildWidth = previewWidth * height / previewHeight
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height)
            } else {
                val scaledChildHeight = previewHeight * width / previewWidth
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2)
            }
        }
    }

    fun setCamera(camera: Camera?) {
        this.camera = camera
        if (this.camera != null) {
            supportedPreviewSizes = this.camera!!.parameters.supportedPreviewSizes
            requestLayout()
            val params = this.camera!!.parameters

            val focusModes = params.supportedFocusModes
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                params.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
                this.camera!!.parameters = params
            }
        }
    }

    private fun getOptimalPreviewSize(sizes: List<Camera.Size>?, width: Int, targetHeight: Int): Camera.Size? {
        val ASPECT_TOLERANCE = 0.1
        val targetRatio = width.toDouble() / targetHeight
        if (sizes == null) return null

        var optimalSize: Camera.Size? = null
        var minDiff = java.lang.Double.MAX_VALUE

        for (size in sizes) {
            val ratio = size.width.toDouble() / size.height
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue
            }

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size
                minDiff = Math.abs(size.height - targetHeight).toDouble()
            }
        }

        if (optimalSize == null) {
            minDiff = java.lang.Double.MAX_VALUE
            for (size in sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size
                    minDiff = Math.abs(size.height - targetHeight).toDouble()
                }
            }
        }

        if (optimalSize == null) {
            optimalSize = sizes[0]
        }

        return optimalSize
    }

    private fun generateProjectionMatrix() {
        val ratio = this.cameraWidth.toFloat() / this.cameraHeight
        val OFFSET = 0
        val LEFT = -ratio
        val BOTTOM = -1f
        val TOP = 1f
        Matrix.frustumM(projectionMatrix, OFFSET, LEFT, ratio, BOTTOM, TOP, Z_NEAR, Z_FAR)
    }

    companion object {
        private const val Z_NEAR = 0.5f
        private const val Z_FAR = 2000f
    }

}
