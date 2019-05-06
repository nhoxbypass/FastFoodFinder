@file:JvmName("FileUtils")
@file:JvmMultifileClass

package com.iceteaviet.fastfoodfinder.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import java.io.IOException

/**
 * Created by tom on 7/19/18.
 */

@Throws(IOException::class)
fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

    val cursor = context.contentResolver.query(uri,
            filePathColumn, null, null, null)
    cursor!!.moveToFirst()
    cursor.close()

    var bmp: Bitmap? = null
    try {
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor)

        parcelFileDescriptor.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }


    return bmp
}
