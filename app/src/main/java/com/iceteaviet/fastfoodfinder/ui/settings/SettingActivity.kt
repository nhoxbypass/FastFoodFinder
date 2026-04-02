package com.iceteaviet.fastfoodfinder.ui.settings

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.databinding.ActivitySettingBinding
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.ui.settings.discountnotify.DiscountNotifyDialog
import com.iceteaviet.fastfoodfinder.utils.openLoginActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class SettingActivity : BaseActivity() {

    private val viewModel: SettingViewModel by viewModels()

    private lateinit var binding: ActivitySettingBinding

    private lateinit var txtShareApp: TextView
    private lateinit var txtChangeMetric: TextView
    private lateinit var txtEditProfile: TextView
    private lateinit var txtChangePassword: TextView
    private lateinit var txtChangeEmail: TextView
    private lateinit var txtSetNotification: TextView
    private lateinit var txtSetEmailNotification: TextView
    private lateinit var layoutUpdateDb: LinearLayout
    private lateinit var progressBarUpdateDb: ProgressBar
    private lateinit var imageUpdateDb: ImageView
    private lateinit var txtAboutApp: TextView
    private lateinit var txtRateApp: TextView
    private lateinit var txtFeedBack: TextView
    private lateinit var txtPrivacyPolicy: TextView
    private lateinit var txtTermOfUse: TextView
    private lateinit var txtSignOut: TextView
    private lateinit var swChangeLanguage: SwitchCompat
    private lateinit var tvSettingLanguage: TextView

    override val layoutId: Int
        get() = R.layout.activity_setting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupEventListeners()
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
                    swChangeLanguage.isChecked = !state.isVietnamese

                    if (state.showSignOutButton) {
                        txtSignOut.visibility = View.VISIBLE
                    } else {
                        txtSignOut.visibility = View.INVISIBLE
                    }

                    imageUpdateDb.visibility = if (state.showLoadingProgressIndicator) View.GONE else View.VISIBLE
                    progressBarUpdateDb.visibility = if (state.showLoadingProgressIndicator) View.VISIBLE else View.GONE

                    when (state.event) {
                        is SettingEvent.Idle -> {}
                        is SettingEvent.LoadLanguage -> {
                            loadLanguage(state.event.languageCode)
                            viewModel.markEventConsumed()
                        }
                        is SettingEvent.OpenLogin -> {
                            openLoginActivity(this@SettingActivity)
                            finish()
                            viewModel.markEventConsumed()
                        }
                        is SettingEvent.ShowSuccessLoadingToast -> {
                            Toast.makeText(this@SettingActivity, getString(R.string.update_database_successfull) + state.event.message, Toast.LENGTH_SHORT).show()
                            viewModel.markEventConsumed()
                        }
                        is SettingEvent.ShowFailedLoadingToast -> {
                            Toast.makeText(this@SettingActivity, getString(R.string.update_database_failed) + state.event.message, Toast.LENGTH_SHORT).show()
                            viewModel.markEventConsumed()
                        }
                    }
                }
            }
        }
    }

    private fun loadLanguage(languageToLoad: String) {
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val configuration = Configuration()
        configuration.setLocale(locale)
        baseContext.resources.updateConfiguration(configuration, baseContext.resources.displayMetrics)
        refreshUI()
    }

    private fun setupUI() {
        findViews()
    }

    private fun findViews() {
        txtShareApp = binding.tvSettingShareApp
        txtChangeMetric = binding.tvSettingChangeMetric
        txtEditProfile = binding.tvSettingEditProfile
        txtChangePassword = binding.tvSettingChangePassword
        txtChangeEmail = binding.tvSettingChangeEmail
        txtSetNotification = binding.tvSettingNotification
        txtSetEmailNotification = binding.tvSettingNotificationEmail
        layoutUpdateDb = binding.llSettingUpdateDb
        progressBarUpdateDb = binding.progressBarUpdateDb
        imageUpdateDb = binding.ivUpdateDb
        txtAboutApp = binding.tvSettingAboutApp
        txtRateApp = binding.tvSettingRateApp
        txtFeedBack = binding.tvSettingFeedback
        txtPrivacyPolicy = binding.tvSettingPrivacyPolicy
        txtTermOfUse = binding.tvSettingTermOfUse
        txtSignOut = binding.tvSettingSignOut
        swChangeLanguage = binding.swLanguages
        tvSettingLanguage = binding.tvSettingEnglish
    }

    private fun refreshUI() {
        txtShareApp.setText(R.string.share_app_with_friends)
        txtChangeMetric.setText(R.string.use_metric_units)
        txtEditProfile.setText(R.string.edit_your_profile)
        txtChangePassword.setText(R.string.change_your_password)
        txtChangeEmail.setText(R.string.change_your_email)
        txtSetNotification.setText(R.string.set_notifications)
        tvSettingLanguage.setText(R.string.english)
        txtAboutApp.setText(R.string.about_fastfood_finder)
        txtRateApp.setText(R.string.rate_app)
        txtFeedBack.setText(R.string.send_feedback)
        txtPrivacyPolicy.setText(R.string.privacy_policy)
        txtTermOfUse.setText(R.string.terms_of_use)
        txtSignOut.setText(R.string.sign_out)
    }

    private fun setupEventListeners() {
        txtSignOut.setOnClickListener {
            viewModel.onSignOutClicked()
        }

        swChangeLanguage.setOnClickListener {
            viewModel.onLanguageChanged()
        }

        tvSettingLanguage.setOnClickListener {
            viewModel.onLanguageChanged()
        }

        txtSetNotification.setOnClickListener {
            val dlg = DiscountNotifyDialog.newInstance()
            dlg.show(supportFragmentManager, "dialog-filter")
        }

        layoutUpdateDb.setOnClickListener {
            viewModel.onLoadStoreFromServer()
        }
    }
}