package com.iceteaviet.fastfoodfinder.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.content.pm.SigningInfo
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.security.MessageDigest

class AppPackageUtilsTest {
    private lateinit var context: Context
    private lateinit var packageManager: PackageManager
    private lateinit var packageInfo: PackageInfo
    private lateinit var signingInfo: SigningInfo
    private lateinit var certSignature: Signature

    @Before
    fun setUp() {
        // setup dependencies
        context = mock(Context::class.java)
        packageManager = mock(PackageManager::class.java)
        packageInfo = mock(PackageInfo::class.java)
        signingInfo = mock(SigningInfo::class.java)
        certSignature = mock(Signature::class.java)

        // setup mocks
        `when`(context.packageManager).thenReturn(packageManager)
        `when`(context.packageName).thenReturn("com.example.app")
        `when`(packageManager.getPackageInfo("com.example.app", PackageManager.GET_SIGNING_CERTIFICATES))
            .thenReturn(packageInfo)

        // Reset the cached signature
        cachedAppSignature = ""
    }

    @Test
    fun `returns empty string when signing info is null`() {
        packageInfo.signingInfo = null

        val result = getAppSignatureSHA1(context)

        assertThat(result).isEmpty()
    }

    @Test
    fun `returns empty string when signature history is null`() {
        `when`(signingInfo.signingCertificateHistory).thenReturn(null)
        packageInfo.signingInfo = signingInfo

        val result = getAppSignatureSHA1(context)

        assertThat(result).isEmpty()
    }

    @Test
    fun `returns empty string when signature history is empty`() {
        `when`(signingInfo.signingCertificateHistory).thenReturn(emptyArray())
        packageInfo.signingInfo = signingInfo

        val result = getAppSignatureSHA1(context)

        assertThat(result).isEmpty()
    }

    @Test
    fun `returns valid SHA1 when signature is present`() {
        val fakeCert = byteArrayOf(1, 2, 3, 4, 5)
        `when`(certSignature.toByteArray()).thenReturn(fakeCert)
        `when`(signingInfo.signingCertificateHistory).thenReturn(arrayOf(certSignature))
        packageInfo.signingInfo = signingInfo

        val result = getAppSignatureSHA1(context)

        val expectedSha1 = MessageDigest.getInstance("SHA1").digest(fakeCert).joinToString("") {
            String.format("%02x", it)
        }
        assertThat(result).isEqualTo(expectedSha1)
    }

    @Test
    fun `returns cached SHA1`() {
        cachedAppSignature = "cached string"

        val fakeCert = byteArrayOf(1, 2, 3, 4, 5)
        `when`(certSignature.toByteArray()).thenReturn(fakeCert)
        `when`(signingInfo.signingCertificateHistory).thenReturn(arrayOf(certSignature))
        packageInfo.signingInfo = signingInfo

        val result = getAppSignatureSHA1(context)

        assertThat(result).isEqualTo("cached string")
    }
}