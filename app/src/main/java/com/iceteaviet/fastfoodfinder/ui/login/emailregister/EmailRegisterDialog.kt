package com.iceteaviet.fastfoodfinder.ui.login.emailregister

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
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.ui.custom.processbutton.ActionProcessButton
import kotlinx.android.synthetic.main.dialog_register.*

/**
 * Created by nhoxbypass on 03/29/2018.
 */
class EmailRegisterDialog : DialogFragment(), EmailRegisterContract.View, View.OnClickListener, View.OnTouchListener {
    override lateinit var presenter: EmailRegisterContract.Presenter

    private var mListener: OnRegisterCompleteListener? = null

    fun setOnRegisterCompleteListener(listener: OnRegisterCompleteListener) {
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
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(@NonNull view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupEventHandlers()
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_sign_up -> {
                presenter.onSignUpButtonClicked(input_email.text.toString(), input_password.text.toString(), input_repassword.text.toString())
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

                R.id.input_repassword -> {
                    input_layout_repassword.error = ""
                }
            }
        }

        return false
    }

    override fun setRegisterButtonProgress(progress: Int) {
        btn_sign_up.progress = progress
    }

    override fun setInputEnabled(enabled: Boolean) {
        input_layout_email.isEnabled = enabled
        input_layout_password.isEnabled = enabled
        input_layout_repassword.isEnabled = enabled
        input_email.isEnabled = enabled
        input_password.isEnabled = enabled
        input_repassword.isEnabled = enabled
    }

    override fun showInvalidPasswordError() {
        input_layout_password.error = getString(R.string.invalid_password)
    }

    override fun showInvalidRePasswordError() {
        input_layout_repassword.error = getString(R.string.confirm_pwd_not_match)
    }

    override fun showInvalidEmailError() {
        input_layout_email.error = getString(R.string.invalid_email)
    }

    override fun notifyRegisterSuccess(user: User) {
        mListener?.onSuccess(user, this)
    }

    override fun notifyLoginError(e: Throwable) {
        mListener?.onError(e)
    }

    private fun setupUI() {
        btn_sign_up.setMode(ActionProcessButton.Mode.ENDLESS)
    }

    private fun setupEventHandlers() {
        btn_sign_up.setOnClickListener(this)
        input_email.setOnTouchListener(this)
        input_password.setOnTouchListener(this)
        input_repassword.setOnTouchListener(this)
    }

    interface OnRegisterCompleteListener {
        fun onSuccess(user: User, dialog: EmailRegisterDialog)
        fun onError(e: Throwable)
    }

    companion object {
        fun newInstance(): EmailRegisterDialog {
            val frag = EmailRegisterDialog()
            val args = Bundle()
            frag.arguments = args
            frag.presenter = EmailRegisterPresenter(App.getDataManager(), frag)
            return frag
        }
    }

}
