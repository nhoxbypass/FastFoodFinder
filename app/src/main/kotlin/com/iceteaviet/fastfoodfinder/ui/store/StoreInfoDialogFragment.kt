package com.iceteaviet.fastfoodfinder.ui.store

import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.store.StoreDetailActivity.Companion.KEY_STORE
import com.iceteaviet.fastfoodfinder.utils.*
import kotlinx.android.synthetic.main.fragment_store_info.*

/**
 * Created by taq on 26/11/2016.
 */

class StoreInfoDialogFragment : DialogFragment() {

    lateinit var cdvh: StoreDetailAdapter.CallDirectionViewHolder
    lateinit var tvStoreName: TextView
    lateinit var tvViewDetail: TextView
    lateinit var tvStoreAddress: TextView
    lateinit var vCallDirection: View
    lateinit var btnAddToFavorite: Button

    private var store: Store? = null

    private var mListener: StoreDialogActionListener? = null

    fun setDialogListen(listener: StoreDialogActionListener) {
        mListener = listener
    }

    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_store_info, container)
    }

    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvStoreName = store_name
        tvViewDetail = view_detail
        tvStoreAddress = store_address
        vCallDirection = call_direction
        btnAddToFavorite = btn_fav

        cdvh = StoreDetailAdapter.CallDirectionViewHolder(vCallDirection) // TODO: Check this !!

        if (arguments != null)
            arguments?.let { store = it.getParcelable(KEY_STORE) }

        if (store == null)
            return

        tvStoreName.text = store!!.title
        tvStoreAddress.text = store!!.address
        tvViewDetail.setOnClickListener {
            store?.let { activity?.startActivity(StoreDetailActivity.getIntent(context!!, it)) }
        }

        cdvh.btnCall.setOnClickListener {
            if (!isEmpty(store!!.tel)) {
                if (isCallPhonePermissionGranted(context!!))
                    startActivity(getCallIntent(store!!.tel!!))
                else
                    requestCallPhonePermission(this@StoreInfoDialogFragment)
            } else {
                Toast.makeText(activity, R.string.store_no_phone_numb, Toast.LENGTH_SHORT).show()
            }
        }
        cdvh.btnDirection.setOnClickListener { mListener!!.onDirection(store) }

        btnAddToFavorite.setOnClickListener {
            mListener!!.onAddToFavorite(store!!.id)
            dismiss()
        }
    }

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
    }

    override fun onResume() {
        val window = dialog.window
        val size = Point()
        val display = window!!.windowManager.defaultDisplay
        display.getSize(size)
        window.setLayout((0.8 * size.x).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
        super.onResume()
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CALL_PHONE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(getCallIntent(store!!.tel!!))
                } else {
                    Toast.makeText(activity, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
            }

            else -> {
            }
        }
    }

    // tên chuối thiệt
    interface StoreDialogActionListener {
        fun onDirection(store: Store?)

        fun onAddToFavorite(storeId: Int)
    }

    companion object {
        fun newInstance(store: Store): StoreInfoDialogFragment {
            val args = Bundle()
            args.putParcelable(KEY_STORE, store)
            val fragment = StoreInfoDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
