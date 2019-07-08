package com.iceteaviet.fastfoodfinder.utils


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.iceteaviet.fastfoodfinder.utils.extension.getConnectivityManager
import com.iceteaviet.fastfoodfinder.utils.shell.AppShell
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.mockito.PowerMockito.`when`
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.io.IOException


/**
 * Created by tom on 2019-06-12.
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(AppShell::class)
class NetworkUtilsTest {
    @Mock
    private lateinit var mockRuntime: Runtime

    @Mock
    private lateinit var mockProcess: Process

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockConnectivityManager: ConnectivityManager

    @Mock
    private lateinit var mockNetworkInfo: NetworkInfo

    @Before
    fun setup() {
        PowerMockito.mockStatic(Runtime::class.java)
    }

    @Test
    fun isInternetConnectedTest_ioException() {
        val ex = IOException()
        `when`(Runtime.getRuntime()).thenReturn(mockRuntime)

        `when`(mockRuntime.exec("ping -c 1 google.com")).thenThrow(ex)
        assertThat(isInternetConnected()).isFalse()
    }

    @Test
    fun isInternetConnectedTest_securityException() {
        val ex = SecurityException()
        `when`(Runtime.getRuntime()).thenReturn(mockRuntime)

        `when`(mockRuntime.exec("ping -c 1 google.com")).thenThrow(ex)
        assertThat(isInternetConnected()).isFalse()
    }

    @Test
    fun isInternetConnectedTest_interruptException() {
        val ex = InterruptedException()
        `when`(Runtime.getRuntime()).thenReturn(mockRuntime)

        `when`(mockRuntime.exec("ping -c 1 google.com")).thenReturn(mockProcess)
        `when`(mockProcess.waitFor()).thenThrow(ex)

        assertThat(isInternetConnected()).isFalse()
    }

    @Test
    fun isInternetConnectedTest_waitForNegative() {
        `when`(Runtime.getRuntime()).thenReturn(mockRuntime)

        `when`(mockRuntime.exec("ping -c 1 google.com")).thenReturn(mockProcess)
        `when`(mockProcess.waitFor()).thenReturn(-1)

        assertThat(isInternetConnected()).isFalse()
    }

    @Test
    fun isInternetConnectedTest() {
        `when`(Runtime.getRuntime()).thenReturn(mockRuntime)

        `when`(mockRuntime.exec("ping -c 1 google.com")).thenReturn(mockProcess)
        `when`(mockProcess.waitFor()).thenReturn(0)

        assertThat(isInternetConnected()).isTrue()
    }

    @Test
    fun isNetworkReachableTest_nullConnectivityManager() {
        `when`(mockContext.getConnectivityManager()).thenReturn(null)

        assertThat(isNetworkReachable(mockContext)).isFalse()
    }

    @Test
    fun isNetworkReachableTest_nullNetworkInfo() {
        `when`(mockContext.getConnectivityManager()).thenReturn(mockConnectivityManager)

        `when`(mockConnectivityManager.activeNetworkInfo).thenReturn(null)

        assertThat(isNetworkReachable(mockContext)).isFalse()
    }

    @Test
    fun isNetworkReachableTest_validNetworkInfo_notConnected() {
        `when`(mockContext.getConnectivityManager()).thenReturn(mockConnectivityManager)

        `when`(mockConnectivityManager.activeNetworkInfo).thenReturn(mockNetworkInfo)
        `when`(mockNetworkInfo.isConnected).thenReturn(false)

        assertThat(isNetworkReachable(mockContext)).isFalse()
    }

    @Test
    fun isNetworkReachableTest_validNetworkInfo_connected() {
        `when`(mockContext.getConnectivityManager()).thenReturn(mockConnectivityManager)

        `when`(mockConnectivityManager.activeNetworkInfo).thenReturn(mockNetworkInfo)
        `when`(mockNetworkInfo.isConnected).thenReturn(true)

        assertThat(isNetworkReachable(mockContext)).isTrue()
    }
}