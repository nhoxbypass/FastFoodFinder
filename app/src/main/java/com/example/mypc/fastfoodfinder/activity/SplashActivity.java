package com.example.mypc.fastfoodfinder.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mypc.fastfoodfinder.R;

public class SplashActivity extends AppCompatActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1500;
    private final String KEY_LOGIN = "login_displayed";
    SharedPreferences mSharedPreferences;
    boolean isDisplayedLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AppConfig appConfig = new AppConfig();
        appConfig.execute();

    }

    class AppConfig extends AsyncTask<Void, Void, Class<?>>
    {
        @Override
        protected Class<?> doInBackground(Void... voids) {
            mSharedPreferences = getSharedPreferences("FastfoodFinder", 0);
            isDisplayedLogin = mSharedPreferences.getBoolean(KEY_LOGIN,false);

            try {
                Thread.sleep(SPLASH_DISPLAY_LENGTH);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!isDisplayedLogin) {
                return MainActivity.class;
            } else {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean(KEY_LOGIN, true);
                editor.apply();

                return LoginActivity.class;
            }
        }

        @Override
        protected void onPostExecute(Class<?> aClass) {
            super.onPostExecute(aClass);
            Intent intent = new Intent(SplashActivity.this, aClass);
            startActivity(intent);
            finish();
        }
    }
}
