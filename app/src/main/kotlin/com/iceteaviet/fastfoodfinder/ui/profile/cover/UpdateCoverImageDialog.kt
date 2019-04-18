package com.iceteaviet.fastfoodfinder.ui.profile.cover

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.iceteaviet.fastfoodfinder.R
import kotlinx.android.synthetic.main.dialog_choose_image.*
import java.io.IOException

/**
 * Created by MyPC on 11/29/2016.
 */
class UpdateCoverImageDialog : DialogFragment() {
    private var chosenImageId = 0
    private var mBmp: Bitmap? = null

    private var mListener: OnButtonClickListener? = null

    fun setOnButtonClickListener(listener: OnButtonClickListener) {
        mListener = listener
    }

    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_choose_image, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(@NonNull view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBrowser!!.setOnClickListener {
            val i = Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, RESULT_LOAD_IMAGE)
        }

        ivOne!!.setOnClickListener {
            ivChosenImage!!.setImageResource(R.drawable.profile_sample_background)
            chosenImageId = R.drawable.profile_sample_background
        }
        ivTwo!!.setOnClickListener {
            ivChosenImage!!.setImageResource(R.drawable.all_sample_avatar)
            chosenImageId = R.drawable.all_sample_avatar
        }
        ivThree!!.setOnClickListener {
            ivChosenImage!!.setImageResource(R.drawable.profile_sample_background_3)
            chosenImageId = R.drawable.profile_sample_background_3
        }
        ivFour!!.setOnClickListener {
            ivChosenImage!!.setImageResource(R.drawable.profile_sample_background_4)
            chosenImageId = R.drawable.profile_sample_background_4
        }
        ivFive!!.setOnClickListener {
            ivChosenImage!!.setImageResource(R.drawable.profile_sample_background_5)
            chosenImageId = R.drawable.profile_sample_background_5
        }
        ivSix!!.setOnClickListener {
            ivChosenImage!!.setImageResource(R.drawable.profile_sample_background_6)
            chosenImageId = R.drawable.profile_sample_background_6
        }

        btnDone!!.setOnClickListener {
            mListener?.onOkClick(chosenImageId, mBmp)
            dismiss()
        }

        btnCancel!!.setOnClickListener {
            mListener?.onCancelClick()
            dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RESULT_LOAD_IMAGE -> if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                val selectedImage = data.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

                val cursor = activity!!.contentResolver.query(selectedImage!!,
                        filePathColumn, null, null, null)
                cursor!!.moveToFirst()

                //int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                //String picturePath = cursor.getString(columnIndex);
                cursor.close()

                var bmp: Bitmap? = null
                try {
                    bmp = getBitmapFromUri(selectedImage)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                ivChosenImage!!.setImageBitmap(bmp)
                chosenImageId = -1
                mBmp = bmp
            }
            else -> {
            }
        }
    }

    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val parcelFileDescriptor = activity!!.contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }

    interface OnButtonClickListener {
        fun onOkClick(Id: Int, bmp: Bitmap?)
        fun onCancelClick()
    }

    companion object {
        private const val RESULT_LOAD_IMAGE = 1

        fun newInstance(): UpdateCoverImageDialog {
            val frag = UpdateCoverImageDialog()
            val args = Bundle()
            frag.arguments = args
            return frag
        }
    }

}
