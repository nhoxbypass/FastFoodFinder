package com.iceteaviet.fastfoodfinder.ui.splash

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.ui.login.LoginActivity
import com.iceteaviet.fastfoodfinder.ui.main.MainActivity
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SplashActivity : AppCompatActivity() {
    private var dataManager: DataManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        dataManager = App.getDataManager()

        if (dataManager!!.getPreferencesHelper().getAppLaunchFirstTime()!! || dataManager!!.getPreferencesHelper().getNumberOfStores() == 0) {
            // Download data from Firebase and store in Realm
            dataManager!!.loadStoresFromServer(this)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<List<Store>> {
                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onSuccess(storeList: List<Store>) {
                            dataManager!!.getPreferencesHelper().setAppLaunchFirstTime(false)
                            dataManager!!.getPreferencesHelper().setNumberOfStores(storeList.size)
                            dataManager!!.getLocalStoreDataSource().setStores(storeList)

                            Toast.makeText(this@SplashActivity, R.string.update_database_successfull, Toast.LENGTH_SHORT).show()
                            startMyActivity(LoginActivity::class.java)
                        }

                        override fun onError(e: Throwable) {
                            dataManager!!.signOut()
                            e.printStackTrace()
                            startMyActivity(MainActivity::class.java)
                        }
                    })
        } else {
            if (dataManager!!.isSignedIn()) {
                // User still signed in
                dataManager!!.getRemoteUserDataSource().getUser(dataManager!!.getCurrentUserUid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(object : SingleObserver<User> {
                            override fun onSubscribe(d: Disposable) {

                            }

                            override fun onSuccess(user: User) {
                                dataManager!!.setCurrentUser(user)
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                            }
                        })

                startMyActivity(MainActivity::class.java)
            } else {
                startMyActivity(MainActivity::class.java)
            }
        }
    }

    private fun startMyActivity(activity: Class<*>) {
        val intent = Intent(this@SplashActivity, activity)
        startActivity(intent)
        finish()
    }
}
