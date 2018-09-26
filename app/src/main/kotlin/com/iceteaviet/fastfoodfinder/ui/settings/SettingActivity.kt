package com.iceteaviet.fastfoodfinder.ui.settings

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.BuildConfig
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.login.LoginActivity
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_setting.*
import java.util.*

class SettingActivity : AppCompatActivity() {
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

    private var dataManager: DataManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

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

        dataManager = App.getDataManager()

        val pref = this.getSharedPreferences(
                BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

        isVietnamese = pref.getBoolean(KEY_LANGUAGE, false)

        if (!isVietnamese) {
            swChangeLanguage.isChecked = true
        }
        // Initialize Firebase Auth
        if (!dataManager!!.isSignedIn()) {
            txtSignOut.isEnabled = false
        }

        setupEventListeners(pref)
    }

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

    private fun setupEventListeners(pref: SharedPreferences) {
        txtSignOut.setOnClickListener {
            App.getDataManager().signOut()
            val intent = Intent(this@SettingActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        swChangeLanguage.setOnClickListener {
            if (isVietnamese) {
                swChangeLanguage.isChecked = true
                isVietnamese = false
                loadLanguage("vi")

            } else {
                loadLanguage("en")
                swChangeLanguage.isChecked = false
                isVietnamese = true
            }

            val editor = pref.edit()
            editor.putBoolean(KEY_LANGUAGE, isVietnamese)
            editor.apply()
        }

        tvSettingLanguage.setOnClickListener {
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

        txtSetNotification.setOnClickListener {
            val fm = supportFragmentManager
            val dlg = StoreFilterDialogFragment.newInstance()
            dlg.show(fm, "dialog-filter")
        }

        layoutUpdateDb.setOnClickListener {
            App.getDataManager().loadStoresFromServer(this@SettingActivity)
                    .subscribe(object : SingleObserver<List<Store>> {
                        override fun onSubscribe(d: Disposable) {
                            imageUpdateDb.visibility = View.GONE
                            progressBarUpdateDb.visibility = View.VISIBLE
                        }

                        override fun onSuccess(storeList: List<Store>) {
                            dataManager!!.getLocalStoreDataSource().setStores(storeList)
                            Toast.makeText(this@SettingActivity, R.string.update_database_successfull, Toast.LENGTH_SHORT).show()
                            imageUpdateDb.visibility = View.VISIBLE
                            progressBarUpdateDb.visibility = View.GONE
                        }

                        override fun onError(e: Throwable) {
                            Toast.makeText(this@SettingActivity, getString(R.string.update_database_failed) + e.message, Toast.LENGTH_SHORT).show()
                            imageUpdateDb.visibility = View.VISIBLE
                            progressBarUpdateDb.visibility = View.GONE
                        }
                    })
        }
    }

    companion object {
        const val KEY_LANGUAGE = "lang"
    }
}
