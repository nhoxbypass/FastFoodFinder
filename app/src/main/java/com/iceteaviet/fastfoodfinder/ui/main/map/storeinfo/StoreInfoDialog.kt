package com.iceteaviet.fastfoodfinder.ui.main.map.storeinfo

import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.domain.model.Store
import com.iceteaviet.fastfoodfinder.databinding.FragmentStoreInfoBinding
import com.iceteaviet.fastfoodfinder.ui.store.StoreDetailActivity.Companion.KEY_STORE
import com.iceteaviet.fastfoodfinder.ui.store.StoreDetailAdapter
import com.iceteaviet.fastfoodfinder.utils.REQUEST_CALL_PHONE
import com.iceteaviet.fastfoodfinder.utils.isCallPhonePermissionGranted
import com.iceteaviet.fastfoodfinder.utils.makeNativeCall
import com.iceteaviet.fastfoodfinder.utils.requestCallPhonePermission
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StoreInfoDialog : DialogFragment() {

    private val viewModel: StoreInfoViewModel by viewModels()

    private lateinit var binding: FragmentStoreInfoBinding

    lateinit var cdvh: StoreDetailAdapter.CallDirectionViewHolder
    lateinit var tvStoreName: TextView
    lateinit var tvViewDetail: TextView
    lateinit var tvStoreAddress: TextView
    lateinit var vCallDirection: View
    lateinit var btnAddToFavorite: Button

    private var mListener: StoreDialogActionListener? = null

    fun setDialogListen(listener: StoreDialogActionListener) {
        mListener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStoreInfoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvStoreName = binding.storeName
        tvViewDetail = binding.viewDetail
        tvStoreAddress = binding.storeAddress
        vCallDirection = binding.callDirection.root
        btnAddToFavorite = binding.btnFav

        cdvh = StoreDetailAdapter.CallDirectionViewHolder(vCallDirection)

        updateNewStoreUI(viewModel.store)

        tvViewDetail.setOnClickListener {
            viewModel.setOnDetailTextViewClick()
        }

        cdvh.btnCall.setOnClickListener {
            if (isCallPhonePermissionGranted(requireContext()))
                viewModel.onMakeCallWithPermission()
            else
                requestCallPhonePermission(this)
        }
        cdvh.btnDirection.setOnClickListener {
            viewModel.onDirectionButtonClick()
        }

        btnAddToFavorite.setOnClickListener {
            viewModel.onAddToFavoriteButtonClick()
        }

        setupObservers()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is StoreInfoEvent.Idle -> {}
                        is StoreInfoEvent.Exit -> {
                            dismiss()
                            viewModel.markEventConsumed()
                        }
                        is StoreInfoEvent.OpenStoreDetail -> {
                            com.iceteaviet.fastfoodfinder.utils.openStoreDetailActivity(requireActivity(), state.store)
                            viewModel.markEventConsumed()
                        }
                        is StoreInfoEvent.MakeNativeCall -> {
                            makeNativeCall(requireActivity(), state.tel)
                            viewModel.markEventConsumed()
                        }
                        is StoreInfoEvent.ShowEmptyTelToast -> {
                            Toast.makeText(activity, R.string.store_no_phone_numb, Toast.LENGTH_SHORT).show()
                            viewModel.markEventConsumed()
                        }
                        is StoreInfoEvent.AddStoreToFavorite -> {
                            mListener?.onAddToFavorite(state.store.id)
                            dismiss()
                            viewModel.markEventConsumed()
                        }
                        is StoreInfoEvent.DirectionChange -> {
                            mListener?.onDirection(state.store)
                            viewModel.markEventConsumed()
                        }
                    }
                }
            }
        }
    }

    private fun updateNewStoreUI(store: Store?) {
        if (store == null) return
        tvStoreName.text = store.title
        tvStoreAddress.text = store.address
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window
        val size = Point()
        window?.let {
            val display = it.windowManager.defaultDisplay
            display.getSize(size)
            it.setLayout((0.8 * size.x).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
            it.setGravity(Gravity.CENTER)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CALL_PHONE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.onMakeCallWithPermission()
                } else {
                    Toast.makeText(activity, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
            }
        }
    }

    interface StoreDialogActionListener {
        fun onDirection(store: Store?)
        fun onAddToFavorite(storeId: Int)
    }

    companion object {
        fun newInstance(store: Store): StoreInfoDialog {
            val args = Bundle()
            args.putParcelable(KEY_STORE, store)
            val fragment = StoreInfoDialog()
            fragment.arguments = args
            return fragment
        }
    }
}