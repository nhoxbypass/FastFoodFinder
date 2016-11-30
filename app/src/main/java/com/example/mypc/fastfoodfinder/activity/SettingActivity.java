package com.example.mypc.fastfoodfinder.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mypc.fastfoodfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity {

    public static final String KEY_LANGUAGE = "lang";

    @BindView(R.id.tv_setting_share_app) TextView shareApp;
    @BindView(R.id.tv_setting_change_metric) TextView changeMetric;
    @BindView(R.id.tv_setting_edit_profile) TextView editProfile;
    @BindView(R.id.tv_setting_change_password) TextView changePassword;
    @BindView(R.id.tv_setting_change_email) TextView changeEmail;
    @BindView(R.id.tv_setting_notification) TextView setNotification;
    @BindView(R.id.tv_setting_notification_email) TextView setEmailNotification;
    @BindView(R.id.tv_setting_about_app) TextView aboutApp;
    @BindView(R.id.tv_setting_rate_app) TextView rateApp;
    @BindView(R.id.tv_setting_feedback) TextView feedBack;
    @BindView(R.id.tv_setting_privacy_policy) TextView privacyPolicy;
    @BindView(R.id.tv_setting_term_of_use) TextView termOfUse;
    @BindView(R.id.tv_setting_sign_out) TextView signOutView;

    @BindView(R.id.sw_languages)
    SwitchCompat swLanguage;

    @BindView(R.id.tv_setting_english)
    TextView tvSettingLanguage;
    private FirebaseAuth mAuth;

    static boolean isVietnamese = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(this);
        final SharedPreferences pref = this.getSharedPreferences(
                "com.example.mypc.fastfoodfinder", Context.MODE_PRIVATE);

        isVietnamese = pref.getBoolean(KEY_LANGUAGE,false);

        if (!isVietnamese){
            swLanguage.setChecked(true);
        }
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth == null || mAuth.getCurrentUser() == null || mAuth.getCurrentUser().isAnonymous())
        {
            signOutView.setEnabled(false);
        }

        signOutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth != null) {
                    mAuth.signOut();
                    Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        swLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVietnamese)
                {
                    swLanguage.setChecked(true);
                    isVietnamese = false;
                    loadLanguage("vi");

                }
                else {
                    loadLanguage("en");
                    swLanguage.setChecked(false);
                    isVietnamese = true;
                }

                SharedPreferences.Editor editor = pref.edit();

                editor.putBoolean(KEY_LANGUAGE,isVietnamese);
                editor.apply();
            }
        });

        tvSettingLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swLanguage.setChecked(true);
                if (isVietnamese)
                {
                    swLanguage.setChecked(true);
                    isVietnamese = false;
                    loadLanguage("vi");
                }
                else {
                    loadLanguage("en");
                    swLanguage.setChecked(false);
                    isVietnamese = true;
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // refresh your views here
        super.onConfigurationChanged(newConfig);
    }

    public void loadLanguage(String lang){
        String languageToLoad = lang;
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setSystemLocale(configuration, locale);
        }else{
            setSystemLocaleLegacy(configuration, locale);
        }

        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());
        changeLanguage();
    }

    @SuppressWarnings("deprecation")
    public Locale getSystemLocaleLegacy(Configuration config){
        return config.locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Locale getSystemLocale(Configuration config){
        return config.getLocales().get(0);
    }

    @SuppressWarnings("deprecation")
    public void setSystemLocaleLegacy(Configuration config, Locale locale){
        config.locale = locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void setSystemLocale(Configuration config, Locale locale){
        config.setLocale(locale);
    }

    public void changeLanguage()
    {
        shareApp.setText(R.string.share_app_with_friends);
        changeMetric.setText(R.string.use_metric_units);
        editProfile.setText(R.string.edit_your_profile);
        changePassword.setText(R.string.change_your_password);
        changeEmail.setText(R.string.change_your_email);
        setNotification.setText(R.string.set_notifications);
        tvSettingLanguage.setText(R.string.english);
        aboutApp.setText(R.string.about_fastfood_finder);
        rateApp.setText(R.string.rate_app);
        feedBack.setText(R.string.send_feedback);
        privacyPolicy.setText(R.string.privacy_policy);
        termOfUse.setText(R.string.terms_of_use);
        signOutView.setText(R.string.sign_out);

    }
}
