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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.databinding.DialogChooseImageBinding
import com.iceteaviet.fastfoodfinder.utils.getBitmapFromUri
import com.iceteaviet.fastfoodfinder.utils.getImagePickerIntent
import com.iceteaviet.fastfoodfinder.utils.ui.getDrawable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UpdateCoverImageDialog : DialogFragment(), View.OnClickListener {

    private val viewModel: UpdateCoverViewModel by viewModels()

    private lateinit var binding: DialogChooseImageBinding

    private var listener: OnButtonClickListener? = null

    fun setOnButtonClickListener(listener: OnButtonClickListener) {
        this.listener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogChooseImageBinding.inflate(inflater, container, false)
        return binding.root
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
        setupObservers()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    if (state.selectedImage != null) {
                        binding.ivChosenImage.setImageDrawable(state.selectedImage)
                    }

                    when (val event = state.event) {
                        is UpdateCoverEvent.Idle -> {}
                        is UpdateCoverEvent.OpenImageFilePicker -> {
                            startActivityForResult(getImagePickerIntent(), RESULT_LOAD_IMAGE)
                            viewModel.markEventConsumed()
                        }
                        is UpdateCoverEvent.DismissWithResult -> {
                            listener?.onOkClick(event.selectedImage)
                            dismiss()
                            viewModel.markEventConsumed()
                        }
                        is UpdateCoverEvent.Cancel -> {
                            listener?.onCancelClick()
                            dismiss()
                            viewModel.markEventConsumed()
                        }
                    }
                }
            }
        }
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
                viewModel.onImageBrowserButtonClick()
            }

            R.id.ivOne -> {
                getDrawable(R.drawable.profile_sample_background)?.let { viewModel.onCoverImageSelect(it) }
            }

            R.id.ivTwo -> {
                getDrawable(R.drawable.all_sample_avatar)?.let { viewModel.onCoverImageSelect(it) }
            }

            R.id.ivThree -> {
                getDrawable(R.drawable.profile_sample_background_3)?.let { viewModel.onCoverImageSelect(it) }
            }

            R.id.ivFour -> {
                getDrawable(R.drawable.profile_sample_background_4)?.let { viewModel.onCoverImageSelect(it) }
            }

            R.id.ivFive -> {
                getDrawable(R.drawable.profile_sample_background_5)?.let { viewModel.onCoverImageSelect(it) }
            }

            R.id.ivSix -> {
                getDrawable(R.drawable.profile_sample_background_6)?.let { viewModel.onCoverImageSelect(it) }
            }

            R.id.btnDone -> {
                viewModel.onDoneButtonClick()
            }

            R.id.btnCancel -> {
                viewModel.onCancelButtonClick()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RESULT_LOAD_IMAGE -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                    val bmp: Bitmap? = getBitmapFromUri(requireActivity(), data.data!!)
                    if (bmp != null)
                        viewModel.onCoverImageSelect(BitmapDrawable(resources, bmp))
                    else
                        Toast.makeText(requireActivity(), getString(R.string.get_image_from_picker_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }
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
            return frag
        }
    }
}