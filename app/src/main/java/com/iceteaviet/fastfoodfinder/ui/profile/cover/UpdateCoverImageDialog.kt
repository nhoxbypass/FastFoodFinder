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
import androidx.fragment.app.DialogFragment
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.databinding.DialogChooseImageBinding
import com.iceteaviet.fastfoodfinder.utils.getBitmapFromUri
import com.iceteaviet.fastfoodfinder.utils.getImagePickerIntent
import com.iceteaviet.fastfoodfinder.utils.ui.getDrawable

/**
 * Created by MyPC on 11/29/2016.
 */
class UpdateCoverImageDialog : DialogFragment(), UpdateCoverContract.View, View.OnClickListener {
    override lateinit var presenter: UpdateCoverContract.Presenter

    /**
     * Views Ref
     */
    private lateinit var binding: DialogChooseImageBinding

    private var listener: OnButtonClickListener? = null

    fun setOnButtonClickListener(listener: OnButtonClickListener) {
        this.listener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogChooseImageBinding.inflate(inflater, container, false)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
        binding.btnBrowser.setOnClickListener(this)

        binding.ivOne.setOnClickListener(this)
        binding.ivTwo.setOnClickListener(this)
        binding.ivThree.setOnClickListener(this)
        binding.ivFour.setOnClickListener(this)
        binding.ivFive.setOnClickListener(this)
        binding.ivSix.setOnClickListener(this)

        binding.btnDone.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)
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
                    val bmp: Bitmap? = getBitmapFromUri(requireActivity(), data.data!!)
                    if (bmp != null)
                        presenter.onCoverImageSelect(BitmapDrawable(resources, bmp))
                    else
                        Toast.makeText(requireActivity(), getString(R.string.get_image_from_picker_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun openImageFilePicker() {
        startActivityForResult(getImagePickerIntent(), RESULT_LOAD_IMAGE)
    }

    override fun setSelectedImage(selectedImage: Drawable) {
        binding.ivChosenImage.setImageDrawable(selectedImage)
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
            frag.presenter = UpdateCoverPresenter(App.getDataManager(), App.getSchedulerProvider(), frag)
            return frag
        }
    }
}