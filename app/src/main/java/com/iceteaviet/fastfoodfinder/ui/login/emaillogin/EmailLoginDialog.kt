package com.iceteaviet.fastfoodfinder.ui.login.emaillogin

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
import kotlinx.android.synthetic.main.dialog_login.*

/**
 * Created by nhoxbypass on 03/29/2018.
 */
class EmailLoginDialog : DialogFragment(), EmailLoginContract.View, View.OnClickListener, View.OnTouchListener {
    override lateinit var presenter: EmailLoginContract.Presenter

    private var mListener: OnLoginCompleteListener? = null

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

    override fun onViewCreated(@NonNull view: View, savedInstanceState: Bundle?) {
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
                presenter.onSignInButtonClicked(input_email.text.toString(), input_password.text.toString())
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

    override fun setLoginButtonProgress(progress: Int) {
        btn_sign_in.progress = progress
    }

    override fun setInputEnabled(enabled: Boolean) {
        input_layout_email.isEnabled = enabled
        input_layout_password.isEnabled = enabled
        input_email.isEnabled = enabled
        input_password.isEnabled = enabled
    }

    override fun showInvalidPasswordError() {
        input_layout_password.error = getString(R.string.invalid_password)
    }

    override fun showInvalidEmailError() {
        input_layout_email.error = getString(R.string.invalid_email)
    }

    override fun notifyLoginSuccess(user: User) {
        mListener?.onSuccess(user, this)
    }

    override fun notifyLoginError(e: Throwable) {
        mListener?.onError(e)
    }

    private fun setupEventHandlers() {
        btn_sign_in.setOnClickListener(this)
        input_email.setOnTouchListener(this)
        input_password.setOnTouchListener(this)
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
