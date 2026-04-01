package com.iceteaviet.fastfoodfinder.ui.login.emaillogin

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
import com.iceteaviet.fastfoodfinder.databinding.DialogLoginBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmailLoginDialog : DialogFragment(), View.OnClickListener, View.OnTouchListener {

    private val viewModel: EmailLoginViewModel by viewModels()
    private lateinit var binding: DialogLoginBinding
    private var mListener: OnLoginCompleteListener? = null

    fun setOnLoginCompleteListener(listener: OnLoginCompleteListener) {
        mListener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEventHandlers()
        setupObservers()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    binding.btnSignIn.progress = state.loginButtonProgress
                    
                    binding.inputLayoutEmail.isEnabled = state.isInputEnabled
                    binding.inputLayoutPassword.isEnabled = state.isInputEnabled
                    binding.inputEmail.isEnabled = state.isInputEnabled
                    binding.inputPassword.isEnabled = state.isInputEnabled

                    if (state.showInvalidPasswordError) {
                        binding.inputLayoutPassword.error = getString(R.string.invalid_password)
                        viewModel.markEventConsumed()
                    }
                    if (state.showInvalidEmailError) {
                        binding.inputLayoutEmail.error = getString(R.string.invalid_email)
                        viewModel.markEventConsumed()
                    }
                    
                    if (state.successUser != null) {
                        mListener?.onSuccess(state.successUser, this@EmailLoginDialog)
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
            R.id.btn_sign_in -> {
                viewModel.onSignInButtonClicked(binding.inputEmail.text.toString(), binding.inputPassword.text.toString())
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
            return frag
        }
    }
}