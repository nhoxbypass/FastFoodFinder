package com.iceteaviet.fastfoodfinder.data.prefs;

/**
 * Created by tom on 7/24/18.
 */
public interface PreferencesHelper {
    void putString(String key, String value);

    String getString(String key, String defaultValue);

    void putBoolean(String key, Boolean value);

    Boolean getBoolean(String key, Boolean defaultValue);

    Boolean getAppLaunchFirstTime();

    void setAppLaunchFirstTime(Boolean isFirstTime);

    int getNumberOfStores();

    void setNumberOfStores(int numberOfStores);

    String getCurrentUserUid();

    void setCurrentUserUid(String uid);
}
