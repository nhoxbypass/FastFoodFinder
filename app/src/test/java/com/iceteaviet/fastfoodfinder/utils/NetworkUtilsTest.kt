package com.iceteaviet.fastfoodfinder.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Created by tom on 2019-06-12.
 */
class NetworkUtilsTest {
    @Test
    fun isInternetConnectedTest() {
        assertThat(isInternetConnected()).isTrue()
    }
}