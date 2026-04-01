package com.iceteaviet.fastfoodfinder.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import com.google.firebase.auth.AuthCredential
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.auth.provider.AuthHelper
import com.iceteaviet.fastfoodfinder.data.auth.provider.AuthRequestListener
import com.iceteaviet.fastfoodfinder.data.auth.provider.GoogleAuthHelper
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.databinding.ActivityLoginBinding
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.ui.login.emaillogin.EmailLoginDialog
import com.iceteaviet.fastfoodfinder.ui.login.emailregister.EmailRegisterDialog
import com.iceteaviet.fastfoodfinder.utils.openMainActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity(), View.OnClickListener {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var binding: ActivityLoginBinding
    private var emailRegisterDialog: EmailRegisterDialog? = null
    private var emailLoginDialog: EmailLoginDialog? = null
    private var googleAuthHelper: AuthHelper<AuthCredential>? = null

    override val layoutId: Int
        get() = R.layout.activity_login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        googleAuthHelper = GoogleAuthHelper(this, getString(R.string.default_web_client_id))

        setupUI()
        setupEventHandlers()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.start()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is LoginUiState.Idle -> { }
                        is LoginUiState.NavigateToMain -> {
                            showMainView()
                            viewModel.markEventConsumed()
                        }
                        is LoginUiState.Exit -> {
                            exit()
                            viewModel.markEventConsumed()
                        }
                        is LoginUiState.ShowSignInFailMessage -> {
                            showSignInFailMessage()
                            viewModel.markEventConsumed()
                        }
                        is LoginUiState.ShowGeneralErrorMessage -> {
                            showGeneralErrorMessage()
                            viewModel.markEventConsumed()
                        }
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null)
            return

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            googleAuthHelper!!.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_skip -> {
                viewModel.onSkipButtonClick()
            }
            R.id.btn_register -> {
                emailRegisterDialog?.show(supportFragmentManager, "dialog-email-register")
            }
            R.id.btn_login -> {
                emailLoginDialog?.show(supportFragmentManager, "dialog-email-login")
            }
            R.id.btn_google_signin -> {
                onGoogleSignInButtonClicked()
            }
        }
    }

    private fun exit() {
        finish()
    }

    private fun showMainView() {
        openMainActivity(this)
        finish()
    }

    private fun showSignInFailMessage() {
        Toast.makeText(this, R.string.authentication_failed, Toast.LENGTH_SHORT).show()
    }

    private fun showGeneralErrorMessage() {
        Toast.makeText(this, R.string.error_general_error_code, Toast.LENGTH_LONG).show()
    }

    private fun setupUI() {
        emailRegisterDialog = EmailRegisterDialog.newInstance()
        emailLoginDialog = EmailLoginDialog.newInstance()
    }

    private fun setupEventHandlers() {
        binding.btnSkip.setOnClickListener(this)
        binding.btnRegister.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
        binding.btnGoogleSignin.setOnClickListener(this)

        emailRegisterDialog?.setOnRegisterCompleteListener(object : EmailRegisterDialog.OnRegisterCompleteListener {
            override fun onSuccess(user: User, dialog: EmailRegisterDialog) {
                dialog.dismiss()
                viewModel.onRegisterSuccess(user)
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
            }
        })

        emailLoginDialog?.setOnLoginCompleteListener(object : EmailLoginDialog.OnLoginCompleteListener {
            override fun onSuccess(user: User, dialog: EmailLoginDialog) {
                dialog.dismiss()
                viewModel.onLoginSuccess(user)
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
            }
        })

        googleAuthHelper!!.setAuthRequestListener(object : AuthRequestListener<AuthCredential> {
            override fun onSuccess(authCredential: AuthCredential, fromLastSignIn: Boolean) {
                viewModel.onRequestGoogleAccountSuccess(authCredential, fromLastSignIn)
            }

            override fun onFailed() {
                showSignInFailMessage()
            }
        })
    }

    private fun onGoogleSignInButtonClicked() {
        googleAuthHelper!!.startRequestAuthCredential()
    }

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
        const val RC_GOOGLE_SIGN_IN = 1
    }
}
