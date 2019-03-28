package com.iceteaviet.fastfoodfinder.ui.login

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.ui.custom.processbutton.ActionProcessButton
import com.iceteaviet.fastfoodfinder.utils.isValidEmail
import com.iceteaviet.fastfoodfinder.utils.isValidPassword
import kotlinx.android.synthetic.main.dialog_login.*

/**
 * Created by nhoxbypass on 03/29/2018.
 */
class EmailLoginDialog : DialogFragment(), View.OnClickListener, View.OnTouchListener {
    private var mListener: OnLoginCompleteListener? = null
    private lateinit var dataManager: DataManager

    fun setOnLoginCompleteListener(listener: OnLoginCompleteListener) {
        mListener = listener
    }

    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_login, container, false)
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
        dataManager = App.getDataManager()
        setupEventHandlers()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_sign_up -> {
                onSignUpButtonClicked()
            }

            R.id.input_email -> {
                input_layout_email.error = ""
            }

            R.id.input_password -> {
                input_layout_password.error = ""
            }
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            when (v.id) {
                R.id.input_email -> {
                    input_layout_email.error = ""
                }

                R.id.input_password -> {
                    input_layout_password.error = ""
                }
            }
        }

        return false
    }

    private fun setupEventHandlers() {
        btn_sign_up.setOnClickListener(this)
        input_email.setOnTouchListener(this)
        input_password.setOnTouchListener(this)
    }

    // TODO: Optimize checking logic
    private fun onSignUpButtonClicked() {
        btn_sign_up.setMode(ActionProcessButton.Mode.ENDLESS)
        btn_sign_up.progress = 1
        setInputEnabled(false)

        if (isValidEmail(input_email.text.toString())) {
            if (isValidPassword(input_password.text.toString())) {

            } else {
                input_layout_password.error = getString(R.string.invalid_password)
            }
        } else {
            input_layout_email.error = getString(R.string.invalid_email)
        }

        btn_sign_up.progress = 0
        setInputEnabled(true)
    }

    private fun setInputEnabled(enabled: Boolean) {
        input_layout_email.isEnabled = enabled
        input_layout_password.isEnabled = enabled
        input_email.isEnabled = enabled
        input_password.isEnabled = enabled
    }

    interface OnLoginCompleteListener {
        fun onSuccess()
        fun onErrror()
    }

    companion object {
        private const val RESULT_LOAD_IMAGE = 1

        fun newInstance(): EmailLoginDialog {
            val frag = EmailLoginDialog()
            val args = Bundle()
            frag.arguments = args
            return frag
        }
    }

}
