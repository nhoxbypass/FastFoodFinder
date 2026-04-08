package com.iceteaviet.fastfoodfinder.ui.splash

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.utils.openLoginActivity
import com.iceteaviet.fastfoodfinder.utils.openMainActivity
import com.iceteaviet.fastfoodfinder.utils.openSplashActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private val viewModel: SplashViewModel by viewModels()

    companion object {
        const val SPLASH_DELAY_TIME = 500L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is SplashUiState.Idle -> { }
                        is SplashUiState.NavigateToLogin -> {
                            openLoginScreen()
                            viewModel.markEventConsumed()
                        }
                        is SplashUiState.NavigateToMain -> {
                            openMainScreenWithDelay(state.delayTime)
                            viewModel.markEventConsumed()
                        }
                        is SplashUiState.ShowGeneralError -> {
                            showGeneralErrorMessage()
                            viewModel.markEventConsumed() // Allow consecutive errors
                        }
                        is SplashUiState.ShowRetryDialog -> {
                            showRetryDialog()
                            viewModel.markEventConsumed()
                        }
                        is SplashUiState.Exit -> {
                            exit()
                            viewModel.markEventConsumed()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.start()
    }

    private fun openLoginScreen() {
        openLoginActivity(this)
        finish()
    }

    private fun exit() {
        finish()
    }

    private fun openMainScreenWithDelay(delayTime: Long) {
        if (delayTime > 0) {
            Handler(Looper.getMainLooper())
                .postDelayed({
                    openMainActivity(this)
                    finish()
                }, delayTime)
        } else {
            openMainActivity(this)
            finish()
        }
    }

    private fun restartSplashScreen() {
        openSplashActivity(this)
        finish()
    }

    private fun showGeneralErrorMessage() {
        Toast.makeText(this, R.string.error_general_error_code, Toast.LENGTH_LONG).show()
    }

    private fun showRetryDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_retry_update_db))
            .setMessage(getString(R.string.msg_retry_update_db))
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
                viewModel.loadStoresFromServer()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
                exit()
            }
            .show()
    }

    override val layoutId: Int
        get() = R.layout.activity_splash
}
