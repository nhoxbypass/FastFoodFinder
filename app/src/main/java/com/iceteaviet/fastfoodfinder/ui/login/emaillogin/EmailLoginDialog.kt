package com.iceteaviet.fastfoodfinder.ui.login.emaillogin

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
import com.iceteaviet.fastfoodfinder.databinding.DialogLoginBinding

/**
 * Created by nhoxbypass on 03/29/2018.
 */
class EmailLoginDialog : DialogFragment(), EmailLoginContract.View, View.OnClickListener, View.OnTouchListener {
    override lateinit var presenter: EmailLoginContract.Presenter

    /**
     * Views Ref
     */
    private lateinit var binding: DialogLoginBinding

    private var mListener: OnLoginCompleteListener? = null

    fun setOnLoginCompleteListener(listener: OnLoginCompleteListener) {
        mListener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_login, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        //dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_sign_in -> {
                presenter.onSignInButtonClicked(binding.inputEmail.text.toString(), binding.inputPassword.text.toString())
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
            }
        }

        return false
    }

    override fun setLoginButtonProgress(progress: Int) {
        binding.btnSignIn.progress = progress
    }

    override fun setInputEnabled(enabled: Boolean) {
        binding.inputLayoutEmail.isEnabled = enabled
        binding.inputLayoutPassword.isEnabled = enabled
        binding.inputEmail.isEnabled = enabled
        binding.inputPassword.isEnabled = enabled
    }

    override fun showInvalidPasswordError() {
        binding.inputLayoutPassword.error = getString(R.string.invalid_password)
    }

    override fun showInvalidEmailError() {
        binding.inputLayoutEmail.error = getString(R.string.invalid_email)
    }

    override fun notifyLoginSuccess(user: User) {
        mListener?.onSuccess(user, this)
    }

    override fun notifyLoginError(e: Throwable) {
        mListener?.onError(e)
    }

    private fun setupEventHandlers() {
        binding.btnSignIn.setOnClickListener(this)
        binding.inputEmail.setOnTouchListener(this)
        binding.inputPassword.setOnTouchListener(this)
    }

    interface OnLoginCompleteListener {
        fun onSuccess(user: User, dialog: EmailLoginDialog)
        fun onError(e: Throwable)
    }

    companion object {
        fun newInstance(): EmailLoginDialog {
            val frag = EmailLoginDialog()
            val args = Bundle()
            frag.arguments = args
            frag.presenter = EmailLoginPresenter(App.getDataManager(), App.getSchedulerProvider(), frag)
            return frag
        }
    }
}