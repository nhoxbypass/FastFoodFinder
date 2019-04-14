@file:JvmName("IntentUtils")

package com.iceteaviet.fastfoodfinder.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.iceteaviet.fastfoodfinder.ui.login.LoginActivity
import com.iceteaviet.fastfoodfinder.ui.main.MainActivity

/**
 * Created by Genius Doan on 21/03/2019.
 */


fun openLoginActivity(activity: Activity) {
    val intent = Intent(activity, LoginActivity::class.java)
    activity.startActivity(intent)
    activity.finish()
}


fun openMainActivity(activity: Activity) {
    val intent = Intent(activity, MainActivity::class.java)
    activity.startActivity(intent)
    activity.finish()
}

/**
 * Get phone call Intent
 */
fun newCallIntent(tel: String): Intent {
    val normalizedTel = tel.replace("\\s".toRegex(), "")
    val callIntent = Intent(Intent.ACTION_CALL)
    callIntent.data = Uri.parse("tel:08$normalizedTel")
    return callIntent
}

