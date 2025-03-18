package com.iceteaviet.fastfoodfinder.utils


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.iceteaviet.fastfoodfinder.core.common.ext.getConnectivityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.io.IOException


/**
 * Created by tom on 2019-06-12.
 */
class NetworkUtilsTest {
    private lateinit var mockAnnotations: AutoCloseable

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
        mockAnnotations = MockitoAnnotations.openMocks(this)
    }

    @After
    fun tearDown() {
        mockAnnotations.close()
    }

    @Test
    fun isInternetConnectedTest_ioException() {
        mockStatic(Runtime::class.java).use {
            `when`(Runtime.getRuntime()).thenReturn(mockRuntime)

            val ex = IOException()
            `when`(mockRuntime.exec("ping -c 1 google.com")).thenThrow(ex)
            assertThat(isInternetConnected()).isFalse()
        }
    }

    @Test
    fun isInternetConnectedTest_securityException() {
        mockStatic(Runtime::class.java).use {
            `when`(Runtime.getRuntime()).thenReturn(mockRuntime)

            val ex = SecurityException()
            `when`(mockRuntime.exec("ping -c 1 google.com")).thenThrow(ex)
            assertThat(isInternetConnected()).isFalse()
        }
    }

    @Test
    fun isInternetConnectedTest_interruptException() {
        mockStatic(Runtime::class.java).use {
            `when`(Runtime.getRuntime()).thenReturn(mockRuntime)

            val ex = InterruptedException()
            `when`(mockRuntime.exec("ping -c 1 google.com")).thenReturn(mockProcess)
            `when`(mockProcess.waitFor()).thenThrow(ex)

            assertThat(isInternetConnected()).isFalse()
        }
    }

    @Test
    fun isInternetConnectedTest_waitForNegative() {
        mockStatic(Runtime::class.java).use {
            `when`(Runtime.getRuntime()).thenReturn(mockRuntime)

            `when`(mockRuntime.exec("ping -c 1 google.com")).thenReturn(mockProcess)
            `when`(mockProcess.waitFor()).thenReturn(-1)

            assertThat(isInternetConnected()).isFalse()
        }
    }

    @Test
    fun isInternetConnectedTest() {
        mockStatic(Runtime::class.java).use {
            `when`(Runtime.getRuntime()).thenReturn(mockRuntime)

            `when`(mockRuntime.exec("ping -c 1 google.com")).thenReturn(mockProcess)
            `when`(mockProcess.waitFor()).thenReturn(0)

            assertThat(isInternetConnected()).isTrue()
        }
    }

    @Test
    fun isNetworkReachableTest_nullConnectivityManager() {
        mockStatic(Runtime::class.java).use {
            `when`(mockContext.getConnectivityManager()).thenReturn(null)

            assertThat(isNetworkReachable(mockContext)).isFalse()
        }
    }

    @Test
    fun isNetworkReachableTest_nullNetworkInfo() {
        mockStatic(Runtime::class.java).use {
            `when`(mockContext.getConnectivityManager()).thenReturn(mockConnectivityManager)

            `when`(mockConnectivityManager.activeNetworkInfo).thenReturn(null)

            assertThat(isNetworkReachable(mockContext)).isFalse()
        }
    }

    @Test
    fun isNetworkReachableTest_validNetworkInfo_notConnected() {
        mockStatic(Runtime::class.java).use {
            `when`(mockContext.getConnectivityManager()).thenReturn(mockConnectivityManager)

            `when`(mockConnectivityManager.activeNetworkInfo).thenReturn(mockNetworkInfo)
            `when`(mockNetworkInfo.isConnected).thenReturn(false)

            assertThat(isNetworkReachable(mockContext)).isFalse()
        }
    }

    @Test
    fun isNetworkReachableTest_validNetworkInfo_connected() {
        mockStatic(Runtime::class.java).use {
            `when`(mockContext.getConnectivityManager()).thenReturn(mockConnectivityManager)

            `when`(mockConnectivityManager.activeNetworkInfo).thenReturn(mockNetworkInfo)
            `when`(mockNetworkInfo.isConnected).thenReturn(true)

            assertThat(isNetworkReachable(mockContext)).isTrue()
        }
    }
}