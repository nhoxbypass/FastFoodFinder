package com.iceteaviet.fastfoodfinder.activity.main;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iceteaviet.fastfoodfinder.BuildConfig;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.model.store.Store;
import com.iceteaviet.fastfoodfinder.model.store.StoreDataSource;
import com.iceteaviet.fastfoodfinder.network.FirebaseClient;
import com.iceteaviet.fastfoodfinder.ui.fragment.profile.StoreFilterDialogFragment;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity {

    public static final String KEY_LANGUAGE = "lang";
    @BindView(R.id.tv_setting_share_app)
    TextView txtShareApp;
    @BindView(R.id.tv_setting_change_metric)
    TextView txtChangeMetric;
    @BindView(R.id.tv_setting_edit_profile)
    TextView txtEditProfile;
    @BindView(R.id.tv_setting_change_password)
    TextView txtChangePassword;
    @BindView(R.id.tv_setting_change_email)
    TextView txtChangeEmail;
    @BindView(R.id.tv_setting_notification)
    TextView txtSetNotification;
    @BindView(R.id.tv_setting_notification_email)
    TextView txtSetEmailNotification;
    @BindView(R.id.ll_setting_update_db)
    LinearLayout layoutUpdateDb;
    @BindView(R.id.progress_bar_update_db)
    ProgressBar progressBarUpdateDb;
    @BindView(R.id.iv_update_db)
    ImageView imageUpdateDb;
    @BindView(R.id.tv_setting_about_app)
    TextView txtAboutApp;
    @BindView(R.id.tv_setting_rate_app)
    TextView txtRateApp;
    @BindView(R.id.tv_setting_feedback)
    TextView txtFeedBack;
    @BindView(R.id.tv_setting_privacy_policy)
    TextView txtPrivacyPolicy;
    @BindView(R.id.tv_setting_term_of_use)
    TextView txtTermOfUse;
    @BindView(R.id.tv_setting_sign_out)
    TextView txtSignOut;
    @BindView(R.id.sw_languages)
    SwitchCompat swChangeLanguage;
    @BindView(R.id.tv_setting_english)
    TextView tvSettingLanguage;
    private boolean isVietnamese = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(this);

        final SharedPreferences pref = this.getSharedPreferences(
                BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);

        isVietnamese = pref.getBoolean(KEY_LANGUAGE, false);

        if (!isVietnamese) {
            swChangeLanguage.setChecked(true);
        }
        // Initialize Firebase Auth
        if (FirebaseClient.getInstance().getAuth() == null
                || FirebaseClient.getInstance().getAuth().getCurrentUser() == null
                || (FirebaseClient.getInstance().getAuth().getCurrentUser() != null && FirebaseClient.getInstance().getAuth().getCurrentUser().isAnonymous())) {
            txtSignOut.setEnabled(false);
        }

        txtSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseClient.getInstance().getAuth() != null) {
                    FirebaseClient.getInstance().signOut();
                    Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        swChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVietnamese) {
                    swChangeLanguage.setChecked(true);
                    isVietnamese = false;
                    loadLanguage("vi");

                } else {
                    loadLanguage("en");
                    swChangeLanguage.setChecked(false);
                    isVietnamese = true;
                }

                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean(KEY_LANGUAGE, isVietnamese);
                editor.apply();
            }
        });

        tvSettingLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swChangeLanguage.setChecked(true);
                if (isVietnamese) {
                    swChangeLanguage.setChecked(true);
                    isVietnamese = false;
                    loadLanguage("vi");
                } else {
                    loadLanguage("en");
                    swChangeLanguage.setChecked(false);
                    isVietnamese = true;
                }
            }
        });

        txtSetNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                StoreFilterDialogFragment dlg = StoreFilterDialogFragment.newInstance();
                dlg.show(fm, "dialog-filter");
            }
        });

        layoutUpdateDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseClient.getInstance().readDataFromFirebase(SettingActivity.this, new FirebaseClient.OnGetDataListener() {
                    @Override
                    public void onStart() {
                        imageUpdateDb.setVisibility(View.GONE);
                        progressBarUpdateDb.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onSuccess(List<Store> data) {
                        StoreDataSource.setData(data);
                        Toast.makeText(SettingActivity.this, R.string.update_database_successfull, Toast.LENGTH_SHORT).show();
                        imageUpdateDb.setVisibility(View.VISIBLE);
                        progressBarUpdateDb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailed(String errorMessage) {
                        Toast.makeText(SettingActivity.this, R.string.update_database_failed + errorMessage, Toast.LENGTH_SHORT).show();
                        imageUpdateDb.setVisibility(View.VISIBLE);
                        progressBarUpdateDb.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // refresh your views here
        super.onConfigurationChanged(newConfig);
    }

    public void loadLanguage(String lang) {
        String languageToLoad = lang;
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setSystemLocale(configuration, locale);
        } else {
            setSystemLocaleLegacy(configuration, locale);
        }

        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());
        changeLanguage();
    }

    @SuppressWarnings("deprecation")
    public void setSystemLocaleLegacy(Configuration config, Locale locale) {
        config.locale = locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void setSystemLocale(Configuration config, Locale locale) {
        config.setLocale(locale);
    }

    public void changeLanguage() {
        txtShareApp.setText(R.string.share_app_with_friends);
        txtChangeMetric.setText(R.string.use_metric_units);
        txtEditProfile.setText(R.string.edit_your_profile);
        txtChangePassword.setText(R.string.change_your_password);
        txtChangeEmail.setText(R.string.change_your_email);
        txtSetNotification.setText(R.string.set_notifications);
        tvSettingLanguage.setText(R.string.english);
        txtAboutApp.setText(R.string.about_fastfood_finder);
        txtRateApp.setText(R.string.rate_app);
        txtFeedBack.setText(R.string.send_feedback);
        txtPrivacyPolicy.setText(R.string.privacy_policy);
        txtTermOfUse.setText(R.string.terms_of_use);
        txtSignOut.setText(R.string.sign_out);

    }
}
