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
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.databinding.FragmentStoreInfoBinding
import com.iceteaviet.fastfoodfinder.ui.store.StoreDetailActivity.Companion.KEY_STORE
import com.iceteaviet.fastfoodfinder.ui.store.StoreDetailAdapter
import com.iceteaviet.fastfoodfinder.utils.REQUEST_CALL_PHONE
import com.iceteaviet.fastfoodfinder.utils.isCallPhonePermissionGranted
import com.iceteaviet.fastfoodfinder.utils.makeNativeCall
import com.iceteaviet.fastfoodfinder.utils.requestCallPhonePermission

/**
 * Created by taq on 26/11/2016.
 */

class StoreInfoDialog : DialogFragment(), StoreInfoContract.View {

    override lateinit var presenter: StoreInfoContract.Presenter

    /**
     * Views Ref
     */
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
        return inflater.inflate(R.layout.fragment_store_info, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvStoreName = binding.storeName
        tvViewDetail = binding.viewDetail
        tvStoreAddress = binding.storeAddress
        vCallDirection = binding.callDirection.root
        btnAddToFavorite = binding.btnFav

        cdvh = StoreDetailAdapter.CallDirectionViewHolder(vCallDirection)

        arguments?.let {
            presenter.handleExtras(it.getParcelable(KEY_STORE))
        }

        tvViewDetail.setOnClickListener {
            presenter.setOnDetailTextViewClick()
        }

        cdvh.btnCall.setOnClickListener {
            if (isCallPhonePermissionGranted(requireContext()))
                presenter.onMakeCallWithPermission()
            else
                requestCallPhonePermission(this)
        }
        cdvh.btnDirection.setOnClickListener {
            presenter.onDirectionButtonClick()
        }

        btnAddToFavorite.setOnClickListener {
            presenter.onAddToFavoriteButtonClick()
        }
    }

    override fun onDirectionChange(store: Store) {
        mListener?.onDirection(store)
    }

    override fun addStoreToFavorite(store: Store) {
        mListener?.onAddToFavorite(store.id)
        dismiss()
    }

    override fun makeNativeCall(tel: String) {
        makeNativeCall(requireActivity(), tel)
    }

    override fun showEmptyTelToast() {
        Toast.makeText(activity, R.string.store_no_phone_numb, Toast.LENGTH_SHORT).show()
    }

    override fun openStoreDetailActivity(store: Store?) {
        com.iceteaviet.fastfoodfinder.utils.openStoreDetailActivity(requireActivity(), store!!)
    }

    override fun updateNewStoreUI(store: Store?) {
        if (store == null)
            return

        tvStoreName.text = store.title
        tvStoreAddress.text = store.address
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    override fun onResume() {
        val window = dialog?.window
        val size = Point()
        window?.let {
            val display = it.windowManager.defaultDisplay
            display.getSize(size)
            it.setLayout((0.8 * size.x).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
            it.setGravity(Gravity.CENTER)
        }
        super.onResume()
        presenter.subscribe()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CALL_PHONE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.onMakeCallWithPermission()
                } else {
                    Toast.makeText(activity, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
            }

            else -> {
            }
        }
    }

    override fun exit() {
        this.dismiss()
    }

    // tên chuối thiệt
    interface StoreDialogActionListener {
        fun onDirection(store: Store?)

        fun onAddToFavorite(storeId: Int)
    }

    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()

    }

    companion object {
        fun newInstance(store: Store): StoreInfoDialog {
            val args = Bundle()
            args.putParcelable(KEY_STORE, store)
            val fragment = StoreInfoDialog()
            fragment.arguments = args
            fragment.presenter = StoreInfoPresenter(App.getDataManager(), App.getSchedulerProvider(), fragment)
            return fragment
        }
    }
}