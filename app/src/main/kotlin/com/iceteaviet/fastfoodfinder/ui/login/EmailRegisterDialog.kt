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
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.ui.custom.processbutton.ActionProcessButton
import com.iceteaviet.fastfoodfinder.utils.isValidEmail
import com.iceteaviet.fastfoodfinder.utils.isValidPassword
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_register.*

/**
 * Created by nhoxbypass on 03/29/2018.
 */
class EmailRegisterDialog : DialogFragment(), View.OnClickListener, View.OnTouchListener {
    private var mListener: OnRegisterCompleteListener? = null
    private lateinit var dataManager: DataManager

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

            R.id.input_repassword -> {
                input_layout_repassword.error = ""
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

    private fun setupEventHandlers() {
        btn_sign_up.setOnClickListener(this)
        input_email.setOnTouchListener(this)
        input_password.setOnTouchListener(this)
        input_repassword.setOnTouchListener(this)
    }

    // TODO: Optimize checking logic
    private fun onSignUpButtonClicked() {
        btn_sign_up.setMode(ActionProcessButton.Mode.ENDLESS)
        setRegisterProgressState(1)

        if (isValidEmail(input_email.text.toString())) {
            if (isValidPassword(input_password.text.toString())) {
                if (input_password.text.toString().equals(input_repassword.text.toString())) {
                    // Begin account register
                    dataManager.signUpWithEmailAndPassword(input_email.text.toString(), input_password.text.toString())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : SingleObserver<User> {
                                override fun onSubscribe(d: Disposable) {
                                }

                                override fun onSuccess(user: User) {
                                    setRegisterProgressState(2)
                                    mListener?.onSuccess(user)
                                }

                                override fun onError(e: Throwable) {
                                    e.printStackTrace()
                                    setRegisterProgressState(-1)
                                    mListener?.onError(e)
                                }
                            })
                } else {
                    input_layout_repassword.error = getString(R.string.confirm_pwd_not_match)
                    setRegisterProgressState(0)
                }
            } else {
                input_layout_password.error = getString(R.string.invalid_password)
                setRegisterProgressState(0)
            }
        } else {
            input_layout_email.error = getString(R.string.invalid_email)
            setRegisterProgressState(0)
        }
    }


    private fun setRegisterProgressState(state: Int) {
        when (state) {
            -1 -> {
                btn_sign_up.progress = -1
                setInputEnabled(true)
            }

            0 -> {
                btn_sign_up.progress = 0
                setInputEnabled(true)
            }

            1 -> {
                btn_sign_up.progress = 1
                setInputEnabled(false)
            }

            2 -> {
                btn_sign_up.progress = 100
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

    interface OnRegisterCompleteListener {
        fun onSuccess(user: User)
        fun onError(e: Throwable)
    }

    companion object {
        fun newInstance(): EmailRegisterDialog {
            val frag = EmailRegisterDialog()
            val args = Bundle()
            frag.arguments = args
            return frag
        }
    }

}
