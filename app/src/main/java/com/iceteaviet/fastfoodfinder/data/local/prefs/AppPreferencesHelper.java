package com.iceteaviet.fastfoodfinder.data.local.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tom on 7/24/18.
 * <p>
 * Entry point contains methods that help interacting with application's SharedPreferences
 */

public class AppPreferencesHelper implements PreferencesHelper {
    private static final String PREFS_NAME = "english_now_android";
    private static final String KEY_APP_LAUNCH_FIRST_TIME = "app_launch_first_time";
    private static final String KEY_NUMBER_OF_STORES = "number_of_stores";

    private final SharedPreferences sharedPreferences;

    public AppPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void putString(String key, String value) {
        sharedPreferences.edit()
                .putString(key, value)
                .apply();
    }

    @Override
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    @Override
    public void putBoolean(String key, Boolean value) {
        sharedPreferences.edit()
                .putBoolean(key, value)
                .apply();
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    @Override
    public Boolean getAppLaunchFirstTime() {
        return sharedPreferences.getBoolean(KEY_APP_LAUNCH_FIRST_TIME, true);
    }

    @Override
    public void setAppLaunchFirstTime(Boolean isFirstTime) {
        sharedPreferences.edit()
                .putBoolean(KEY_APP_LAUNCH_FIRST_TIME, isFirstTime)
                .apply();
    }

    @Override
    public int getNumberOfStores() {
        return sharedPreferences.getInt(KEY_NUMBER_OF_STORES, 0);
    }

    @Override
    public void setNumberOfStores(int numberOfStores) {
        sharedPreferences.edit()
                .putInt(KEY_NUMBER_OF_STORES, numberOfStores)
                .apply();
    }
}
