package com.iceteaviet.fastfoodfinder.ui.custom.ar

import android.content.Context
import android.graphics.*
import android.location.Location
import android.opengl.Matrix
import android.view.View
import com.iceteaviet.fastfoodfinder.ui.ar.model.AugmentedPOI
import com.iceteaviet.fastfoodfinder.utils.convertECEFtoENU
import com.iceteaviet.fastfoodfinder.utils.convertWSG84toECEF
import java.util.*

/**
 * Created by Genius Doan on 20/07/2017.
 */

class AROverlayView(context: Context) : View(context) {
    private val paint: Paint
    private val cameraCoordinateVector: FloatArray
    private var rotatedProjectionMatrix = FloatArray(16)
    private var currentLocation: Location? = null
    private val arPoints: MutableList<AugmentedPOI>
    private val arBitmaps: MutableList<Bitmap>

    init {
        arPoints = ArrayList()
        arBitmaps = ArrayList()
        cameraCoordinateVector = FloatArray(4)

        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 60f
    }

    fun setArPoints(list: List<AugmentedPOI>) {
        this.arPoints.clear()
        this.arBitmaps.clear()

        this.arPoints.addAll(list)
        for (i in list.indices) {
            arBitmaps.add(BitmapFactory.decodeResource(resources, list[i].icon))
        }
    }

    fun addArPoint(poi: AugmentedPOI) {
        this.arPoints.add(poi)
        arBitmaps.add(BitmapFactory.decodeResource(resources, poi.icon))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (currentLocation == null) {
            return
        }

        val currentLocationInECEF = convertWSG84toECEF(currentLocation!!)
        var i = 0
        while (i < arPoints.size && i < arBitmaps.size) {
            val pointInECEF = convertWSG84toECEF(arPoints[i].location)
            val pointInENU = convertECEFtoENU(currentLocation!!, currentLocationInECEF, pointInECEF)

            Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0)

            // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
            // if z > 0, the point will display on the opposite
            if (cameraCoordinateVector[2] < 0) {
                val x = (0.5f + cameraCoordinateVector[0] / cameraCoordinateVector[3]) * width
                val y = (0.5f - cameraCoordinateVector[1] / cameraCoordinateVector[3]) * height

                canvas.drawBitmap(arBitmaps[i], x, y, paint)
                canvas.drawText(arPoints[i].name, x - 30 * arPoints[i].name.length / 2, y - 80, paint)
            }

            i++
        }
    }


    fun destroy() {
        for (i in arBitmaps.indices) {
            if (!arBitmaps[i].isRecycled)
                arBitmaps[i].recycle()
        }
    }


    fun updateRotatedProjectionMatrix(rotatedProjectionMatrix: FloatArray) {
        this.rotatedProjectionMatrix = rotatedProjectionMatrix
        this.invalidate()
    }

    fun updateCurrentLocation(currentLocation: Location) {
        this.currentLocation = currentLocation
        this.invalidate()
    }
}
