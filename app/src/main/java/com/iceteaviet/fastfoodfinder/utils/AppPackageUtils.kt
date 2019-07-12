@file:JvmName("AppPackageUtils")

package com.iceteaviet.fastfoodfinder.utils

import android.content.Context
import android.content.pm.PackageManager
import android.text.TextUtils
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


/**
 * Created by Genius Doan on 21/03/2019.
 */

private var signature: String = ""

fun getAppSignatureSHA1(context: Context): String {
    if (!TextUtils.isEmpty(signature))
        return signature

    try {
        val pm = context.packageManager
        val info = pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)

        val signatures = info.signatures
        val cert = signatures[0].toByteArray()
        val md = MessageDigest.getInstance("SHA1")
        val publicKey = md.digest(cert)
        val hexString = StringBuffer()
        for (j in publicKey.indices) {
            val appendString = Integer
                    .toHexString(0xFF and publicKey[j].toInt())
            if (appendString.length == 1)
                hexString.append("0")
            hexString.append(appendString)
            /*if (j<publicKey.length-1) {
                    hexString.append(':');
                }*/
        }
        signature = hexString.toString()
        return signature
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }

    return ""
}