@file:JvmName("AppPackageUtils")

package com.iceteaviet.fastfoodfinder.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.VisibleForTesting
import timber.log.Timber
import java.security.MessageDigest


/**
 * Created by Genius Doan on 21/03/2019.
 */
private const val TAG = "SignatureUtils"

@VisibleForTesting
var cachedAppSignature: String = ""

@Suppress("S5542")
fun getAppSignatureSHA1(context: Context): String {
    if (cachedAppSignature.isNotEmpty()) {
        // return cached app signature
        return cachedAppSignature
    }

    // calculate app signature
    val info = context.packageManager.getPackageInfo(
        context.packageName,
        PackageManager.GET_SIGNING_CERTIFICATES
    )
    val signingInfo = info.signingInfo
    if (signingInfo == null) {
        Timber.e(TAG, "Signing info is null")
        return ""
    }
    val signatures = signingInfo.signingCertificateHistory
    if (signatures.isNullOrEmpty()) {
        Timber.e(TAG, "No signing certificates found")
        return ""
    }

    val cert = signatures[0].toByteArray()
    val md = MessageDigest.getInstance("SHA1")
    val publicKey = md.digest(cert)
    val hexString = StringBuilder()
    for (byte in publicKey) {
        val appendString = String.format("%02x", byte)
        hexString.append(appendString)
    }

    // cache the calculated signature
    return hexString.toString().also {
        cachedAppSignature = it
    }
}