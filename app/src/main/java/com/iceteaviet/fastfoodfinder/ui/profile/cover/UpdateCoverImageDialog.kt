package com.iceteaviet.fastfoodfinder.ui.profile.cover

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.utils.getBitmapFromUri
import com.iceteaviet.fastfoodfinder.utils.getImagePickerIntent
import com.iceteaviet.fastfoodfinder.utils.ui.getDrawable
import kotlinx.android.synthetic.main.dialog_choose_image.*

/**
 * Created by MyPC on 11/29/2016.
 */
class UpdateCoverImageDialog : DialogFragment(), UpdateCoverContract.View, View.OnClickListener {
    override lateinit var presenter: UpdateCoverContract.Presenter

    private var listener: OnButtonClickListener? = null

    fun setOnButtonClickListener(listener: OnButtonClickListener) {
        this.listener = listener
    }

    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_choose_image, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(@NonNull view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEventListeners()
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
    }

    private fun setupEventListeners() {
        btnBrowser.setOnClickListener(this)

        ivOne.setOnClickListener(this)
        ivTwo.setOnClickListener(this)
        ivThree.setOnClickListener(this)
        ivFour.setOnClickListener(this)
        ivFive.setOnClickListener(this)
        ivSix.setOnClickListener(this)

        btnDone.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnBrowser -> {
                presenter.onImageBrowserButtonClick()
            }
            R.id.ivOne -> {
                presenter.onCoverImageSelect(getDrawable(R.drawable.profile_sample_background)!!)
            }
            R.id.ivTwo -> {
                presenter.onCoverImageSelect(getDrawable(R.drawable.all_sample_avatar)!!)
            }
            R.id.ivThree -> {
                presenter.onCoverImageSelect(getDrawable(R.drawable.profile_sample_background_3)!!)
            }
            R.id.ivFour -> {
                presenter.onCoverImageSelect(getDrawable(R.drawable.profile_sample_background_4)!!)
            }
            R.id.ivFive -> {
                presenter.onCoverImageSelect(getDrawable(R.drawable.profile_sample_background_5)!!)
            }
            R.id.ivSix -> {
                presenter.onCoverImageSelect(getDrawable(R.drawable.profile_sample_background_6)!!)
            }
            R.id.btnDone -> {
                presenter.onDoneButtonClick()
            }
            R.id.btnCancel -> {
                cancel()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RESULT_LOAD_IMAGE -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                    val bmp: Bitmap? = getBitmapFromUri(activity!!, data.data!!)
                    if (bmp != null)
                        presenter.onCoverImageSelect(BitmapDrawable(resources, bmp))
                    else
                        Toast.makeText(activity!!, getString(R.string.get_image_from_picker_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun openImageFilePicker() {
        startActivityForResult(getImagePickerIntent(), RESULT_LOAD_IMAGE)
    }

    override fun setSelectedImage(selectedImage: Drawable) {
        ivChosenImage.setImageDrawable(selectedImage)
    }

    override fun dismissWithResult(selectedImage: Drawable?) {
        listener?.onOkClick(selectedImage)
        dismiss()
    }

    override fun cancel() {
        listener?.onCancelClick()
        dismiss()
    }

    interface OnButtonClickListener {
        fun onOkClick(selectedImage: Drawable?)
        fun onCancelClick()
    }

    companion object {
        private const val RESULT_LOAD_IMAGE = 1

        fun newInstance(): UpdateCoverImageDialog {
            val frag = UpdateCoverImageDialog()
            val args = Bundle()
            frag.arguments = args
            frag.presenter = UpdateCoverPresenter(App.getDataManager(), frag)
            return frag
        }
    }

}
