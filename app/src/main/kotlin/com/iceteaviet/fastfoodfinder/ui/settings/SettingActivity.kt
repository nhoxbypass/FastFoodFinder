package com.iceteaviet.fastfoodfinder.ui.settings

import android.annotation.TargetApi
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.utils.openLoginActivity
import kotlinx.android.synthetic.main.activity_setting.*
import java.util.*

class SettingActivity : BaseActivity(), SettingContract.View {
    override lateinit var presenter : SettingContract.Presenter;
    lateinit var txtShareApp: TextView
    lateinit var txtChangeMetric: TextView
    lateinit var txtEditProfile: TextView
    lateinit var txtChangePassword: TextView
    lateinit var txtChangeEmail: TextView
    lateinit var txtSetNotification: TextView
    lateinit var txtSetEmailNotification: TextView
    lateinit var layoutUpdateDb: LinearLayout
    lateinit var progressBarUpdateDb: ProgressBar
    lateinit var imageUpdateDb: ImageView
    lateinit var txtAboutApp: TextView
    lateinit var txtRateApp: TextView
    lateinit var txtFeedBack: TextView
    lateinit var txtPrivacyPolicy: TextView
    lateinit var txtTermOfUse: TextView
    lateinit var txtSignOut: TextView
    lateinit var swChangeLanguage: SwitchCompat
    lateinit var tvSettingLanguage: TextView

    private var isVietnamese = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = SettingPresenter(App.getDataManager(), this)
        txtShareApp = tv_setting_share_app
        txtChangeMetric = tv_setting_change_metric
        txtEditProfile = tv_setting_edit_profile
        txtChangePassword = tv_setting_change_password
        txtChangeEmail = tv_setting_change_email
        txtSetNotification = tv_setting_notification
        txtSetEmailNotification = tv_setting_notification_email
        layoutUpdateDb = ll_setting_update_db
        progressBarUpdateDb = progress_bar_update_db
        imageUpdateDb = iv_update_db
        txtAboutApp = tv_setting_about_app
        txtRateApp = tv_setting_rate_app
        txtFeedBack = tv_setting_feedback
        txtPrivacyPolicy = tv_setting_privacy_policy
        txtTermOfUse = tv_setting_term_of_use
        txtSignOut = tv_setting_sign_out
        swChangeLanguage = sw_languages
        tvSettingLanguage = tv_setting_english
        presenter.onSetupLanguage()
        presenter.onInitSignOutTextView()

        // Initialize Firebase Auth
        setupEventListeners()
    }

    override fun initLanguageView(isVietnamese: Boolean) {
        this.isVietnamese = isVietnamese;
        if (!isVietnamese) {
            swChangeLanguage.isChecked = true
        }
    }

    override fun initSignOutTextView(enabled : Boolean) {
        if (enabled) {
            txtSignOut.visibility = View.VISIBLE
        } else {
            txtSignOut.visibility = View.INVISIBLE
        }
    }
    override val layoutId: Int
        get() = R.layout.activity_setting

    fun loadLanguage(languageToLoad: String) {
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val configuration = Configuration()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setSystemLocale(configuration, locale)
        } else {
            setSystemLocaleLegacy(configuration, locale)
        }

        baseContext.resources.updateConfiguration(configuration,
                baseContext.resources.displayMetrics)
        changeLanguage()
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
    }

    fun setSystemLocaleLegacy(config: Configuration, locale: Locale) {
        config.locale = locale
    }

    @TargetApi(Build.VERSION_CODES.N)
    fun setSystemLocale(config: Configuration, locale: Locale) {
        config.setLocale(locale)
    }

    fun changeLanguage() {
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

    override fun onClickOnLanguageSwitch() {
        if (isVietnamese) {
            swChangeLanguage.isChecked = true
            isVietnamese = false
            loadLanguage("vi")

        } else {
            loadLanguage("en")
            swChangeLanguage.isChecked = false
            isVietnamese = true
        }
    }

    override fun onClickOnLanguageTextView() {
        swChangeLanguage.isChecked = true
        if (isVietnamese) {
            swChangeLanguage.isChecked = true
            isVietnamese = false
            loadLanguage("vi")
        } else {
            loadLanguage("en")
            swChangeLanguage.isChecked = false
            isVietnamese = true
        }
    }


    private fun setupEventListeners() {
        txtSignOut.setOnClickListener {
            presenter.signOut()
            openLoginActivity(this@SettingActivity)
        }

        swChangeLanguage.setOnClickListener {
            presenter.onLanguageSwitchClick()
            presenter.saveLanguagePref(isVietnamese)
        }

        tvSettingLanguage.setOnClickListener {
            presenter.onLanguageTextViewClick()
            presenter.saveLanguagePref(isVietnamese)
        }

        txtSetNotification.setOnClickListener {
            val dlg = StoreFilterDialog.newInstance()
            dlg.show(supportFragmentManager, "dialog-filter")
        }

        layoutUpdateDb.setOnClickListener {
            presenter.onLoadStoreFromServer()
        }
    }


    override fun showSuccessLoadingToast(successMessage : String?) {
        Toast.makeText(this@SettingActivity, getString(R.string.update_database_successfull) + successMessage, Toast.LENGTH_SHORT).show()
    }

    override fun showFailedLoadingToast(failedMessage: String?) {
        Toast.makeText(this@SettingActivity, getString(R.string.update_database_failed) + failedMessage, Toast.LENGTH_SHORT).show()
    }
    override fun updateLoadingProgressView(showProgress: Boolean) {
        imageUpdateDb.visibility =  if (showProgress) View.GONE else View.VISIBLE
        progressBarUpdateDb.visibility = if (showProgress) View.VISIBLE else View.GONE
    }
}
