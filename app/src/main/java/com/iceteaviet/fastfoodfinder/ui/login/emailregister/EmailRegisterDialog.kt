package com.iceteaviet.fastfoodfinder.ui.login.emailregister

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.databinding.DialogRegisterBinding
import com.iceteaviet.fastfoodfinder.ui.custom.processbutton.ActionProcessButton

/**
 * Created by nhoxbypass on 03/29/2018.
 */
class EmailRegisterDialog : DialogFragment(), EmailRegisterContract.View, View.OnClickListener, View.OnTouchListener {
    override lateinit var presenter: EmailRegisterContract.Presenter

    /**
     * Views Ref
     */
    private lateinit var binding: DialogRegisterBinding

    private var mListener: OnRegisterCompleteListener? = null

    fun setOnRegisterCompleteListener(listener: OnRegisterCompleteListener) {
        mListener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogRegisterBinding.inflate(inflater, container, false)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
                presenter.onSignUpButtonClicked(binding.inputEmail.text.toString(), binding.inputPassword.text.toString(), binding.inputRepassword.text.toString())
            }
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            when (v.id) {
                R.id.input_email -> {
                    binding.inputLayoutEmail.error = ""
                }

                R.id.input_password -> {
                    binding.inputLayoutPassword.error = ""
                }

                R.id.input_repassword -> {
                    binding.inputLayoutRepassword.error = ""
                }
            }
        }

        return false
    }

    override fun setRegisterButtonProgress(progress: Int) {
        binding.btnSignUp.progress = progress
    }

    override fun setInputEnabled(enabled: Boolean) {
        binding.inputLayoutEmail.isEnabled = enabled
        binding.inputLayoutPassword.isEnabled = enabled
        binding.inputLayoutRepassword.isEnabled = enabled
        binding.inputEmail.isEnabled = enabled
        binding.inputPassword.isEnabled = enabled
        binding.inputRepassword.isEnabled = enabled
    }

    override fun showInvalidPasswordError() {
        binding.inputLayoutPassword.error = getString(R.string.invalid_password)
    }

    override fun showInvalidRePasswordError() {
        binding.inputLayoutRepassword.error = getString(R.string.confirm_pwd_not_match)
    }

    override fun showInvalidEmailError() {
        binding.inputLayoutEmail.error = getString(R.string.invalid_email)
    }

    override fun notifyRegisterSuccess(user: User) {
        mListener?.onSuccess(user, this)
    }

    override fun notifyLoginError(e: Throwable) {
        mListener?.onError(e)
    }

    private fun setupUI() {
        binding.btnSignUp.setMode(ActionProcessButton.Mode.ENDLESS)
    }

    private fun setupEventHandlers() {
        binding.btnSignUp.setOnClickListener(this)
        binding.inputEmail.setOnTouchListener(this)
        binding.inputPassword.setOnTouchListener(this)
        binding.inputRepassword.setOnTouchListener(this)
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
            frag.presenter = EmailRegisterPresenter(App.getDataManager(), App.getSchedulerProvider(), frag)
            return frag
        }
    }
}