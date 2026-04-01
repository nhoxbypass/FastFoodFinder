package com.iceteaviet.fastfoodfinder.ui.login.emailregister

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.databinding.DialogRegisterBinding
import com.iceteaviet.fastfoodfinder.ui.custom.processbutton.ActionProcessButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmailRegisterDialog : DialogFragment(), View.OnClickListener, View.OnTouchListener {

    private val viewModel: EmailRegisterViewModel by viewModels()
    private lateinit var binding: DialogRegisterBinding
    private var mListener: OnRegisterCompleteListener? = null

    fun setOnRegisterCompleteListener(listener: OnRegisterCompleteListener) {
        mListener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupEventHandlers()
        setupObservers()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    binding.btnSignUp.progress = state.registerButtonProgress
                    
                    binding.inputLayoutEmail.isEnabled = state.isInputEnabled
                    binding.inputLayoutPassword.isEnabled = state.isInputEnabled
                    binding.inputLayoutRepassword.isEnabled = state.isInputEnabled
                    binding.inputEmail.isEnabled = state.isInputEnabled
                    binding.inputPassword.isEnabled = state.isInputEnabled
                    binding.inputRepassword.isEnabled = state.isInputEnabled

                    if (state.showInvalidPasswordError) {
                        binding.inputLayoutPassword.error = getString(R.string.invalid_password)
                        viewModel.markEventConsumed()
                    }
                    if (state.showInvalidRePasswordError) {
                        binding.inputLayoutRepassword.error = getString(R.string.confirm_pwd_not_match)
                        viewModel.markEventConsumed()
                    }
                    if (state.showInvalidEmailError) {
                        binding.inputLayoutEmail.error = getString(R.string.invalid_email)
                        viewModel.markEventConsumed()
                    }

                    if (state.successUser != null) {
                        mListener?.onSuccess(state.successUser, this@EmailRegisterDialog)
                        viewModel.markEventConsumed()
                    }
                    if (state.error != null) {
                        mListener?.onError(state.error)
                        viewModel.markEventConsumed()
                    }
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_sign_up -> {
                viewModel.onSignUpButtonClicked(
                    binding.inputEmail.text.toString(),
                    binding.inputPassword.text.toString(),
                    binding.inputRepassword.text.toString()
                )
            }
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            when (v.id) {
                R.id.input_email -> binding.inputLayoutEmail.error = ""
                R.id.input_password -> binding.inputLayoutPassword.error = ""
                R.id.input_repassword -> binding.inputLayoutRepassword.error = ""
            }
        }
        return false
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
            return frag
        }
    }
}