package com.iceteaviet.fastfoodfinder.ui.login

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.ui.custom.processbutton.ActionProcessButton
import kotlinx.android.synthetic.main.dialog_register.*

/**
 * Created by MyPC on 11/29/2016.
 */
class EmailRegisterDialog : DialogFragment(), View.OnClickListener {
    private var mListener: OnButtonClickListener? = null

    fun setOnButtonClickListener(listener: OnButtonClickListener) {
        mListener = listener
    }

    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_register, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        //dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(@NonNull view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_sign_up.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_sign_up -> {
                btn_sign_up.setMode(ActionProcessButton.Mode.ENDLESS)
                btn_sign_up.progress = 1

                setInputEnabled(false)
            }
        }
    }

    private fun setInputEnabled(enabled: Boolean) {
        input_layout_email.isEnabled = enabled
        input_layout_password.isEnabled = enabled
        input_layout_repassword.isEnabled = enabled
        input_email.isEnabled = enabled
        input_password.isEnabled = enabled
        input_repassword.isEnabled = enabled
    }

    interface OnButtonClickListener {
        fun onOkClick(Id: Int, bmp: Bitmap?)
        fun onCancelClick()
    }

    companion object {
        private const val RESULT_LOAD_IMAGE = 1

        fun newInstance(): EmailRegisterDialog {
            val frag = EmailRegisterDialog()
            val args = Bundle()
            frag.arguments = args
            return frag
        }
    }

}
