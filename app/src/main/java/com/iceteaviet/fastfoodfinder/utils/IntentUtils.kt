@file:JvmName("IntentUtils")

package com.iceteaviet.fastfoodfinder.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import com.iceteaviet.fastfoodfinder.ui.ar.LiveSightActivity
import com.iceteaviet.fastfoodfinder.ui.login.LoginActivity
import com.iceteaviet.fastfoodfinder.ui.main.MainActivity
import com.iceteaviet.fastfoodfinder.ui.routing.MapRoutingActivity
import com.iceteaviet.fastfoodfinder.ui.settings.SettingActivity
import com.iceteaviet.fastfoodfinder.ui.splash.SplashActivity
import com.iceteaviet.fastfoodfinder.ui.store.StoreDetailActivity
import com.iceteaviet.fastfoodfinder.ui.storelist.ListDetailActivity
import com.iceteaviet.fastfoodfinder.ui.storelist.StoreListActivity

/**
 * Created by Genius Doan on 21/03/2019.
 */

fun openSplashActivity(activity: Activity) {
    val intent = Intent(activity, SplashActivity::class.java)
    activity.startActivity(intent)
}

fun openLoginActivity(activity: Activity) {
    val intent = Intent(activity, LoginActivity::class.java)
    activity.startActivity(intent)
}


fun openMainActivity(activity: Activity) {
    val intent = Intent(activity, MainActivity::class.java)
    activity.startActivity(intent)
}

fun openARLiveSightActivity(activity: Activity) {
    val arIntent = Intent(activity, LiveSightActivity::class.java)
    activity.startActivity(arIntent)
}

fun openSettingsActivity(activity: Activity) {
    val settingIntent = Intent(activity, SettingActivity::class.java)
    activity.startActivity(settingIntent)
}

fun openRoutingActivity(activity: Activity, currStore: Parcelable, mapsDirection: Parcelable) {
    val intent = Intent(activity, MapRoutingActivity::class.java)
    val extras = Bundle()
    extras.putParcelable(MapRoutingActivity.KEY_ROUTE_LIST, mapsDirection)
    extras.putParcelable(MapRoutingActivity.KEY_DES_STORE, currStore)
    intent.putExtras(extras)
    activity.startActivity(intent)
}

fun openStoreDetailActivity(activity: Activity, store: Parcelable) {
    val intent = Intent(activity, StoreDetailActivity::class.java)
    val args = Bundle()
    args.putParcelable(StoreDetailActivity.KEY_STORE, store)
    intent.putExtras(args)
    activity.startActivity(intent)
}

fun openListDetailActivity(activity: Activity, userStoreList: Parcelable, photoUrl: String) {
    val intent = Intent(activity, ListDetailActivity::class.java)
    intent.putExtra(ListDetailActivity.KEY_USER_PHOTO_URL, photoUrl)
    intent.putExtra(ListDetailActivity.KEY_USER_STORE_LIST, userStoreList)
    activity.startActivity(intent)
}

fun openStoreListActivity(activity: Activity) {
    val intent = Intent(activity, StoreListActivity::class.java)
    activity.startActivity(intent)
}


/**
 * Get phone call Intent
 */
fun getNativeCallIntent(tel: String): Intent {
    val normalizedTel = tel.replace("\\s".toRegex(), "")
    val callIntent = Intent(Intent.ACTION_CALL)
    callIntent.data = Uri.parse("tel:08$normalizedTel")
    return callIntent
}

fun makeNativeCall(activity: Activity, tel: String) {
    activity.startActivity(getNativeCallIntent(tel))
}

fun getImagePickerIntent(): Intent {
    return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
}

fun getSplashScreenIntent(context: Context): Intent {
    return Intent(context, SplashActivity::class.java)
}
