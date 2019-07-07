package com.iceteaviet.fastfoodfinder.utils.ui

import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.utils.StoreType
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Created by tom on 2019-07-07.
 */
class UiUtilsTest {
    @Test
    fun getStoreLogoDrawableResTest() {
        assertThat(getStoreLogoDrawableRes(StoreType.TYPE_CIRCLE_K)).isEqualTo(R.drawable.logo_circlek_50)
        assertThat(getStoreLogoDrawableRes(StoreType.TYPE_BSMART)).isEqualTo(R.drawable.logo_bsmart_50)
        assertThat(getStoreLogoDrawableRes(StoreType.TYPE_FAMILY_MART)).isEqualTo(R.drawable.logo_familymart_50)
        assertThat(getStoreLogoDrawableRes(StoreType.TYPE_MINI_STOP)).isEqualTo(R.drawable.logo_ministop_50)
        assertThat(getStoreLogoDrawableRes(StoreType.TYPE_SHOP_N_GO)).isEqualTo(R.drawable.logo_shopngo_50)
    }

    @Test
    fun getStoreLogoDrawableResTest_invalidData() {
        assertThat(getStoreLogoDrawableRes(-1)).isEqualTo(R.drawable.logo_circlek_50)
    }

    @Test
    fun getDirectionImageTest() {
        assertThat(getDirectionImage("straight")).isEqualTo(R.drawable.ic_routing_up)
        assertThat(getDirectionImage("turn-left")).isEqualTo(R.drawable.ic_routing_left)
        assertThat(getDirectionImage("turn-right")).isEqualTo(R.drawable.ic_routing_right)
        assertThat(getDirectionImage("merge")).isEqualTo(R.drawable.ic_routing_merge)
    }

    @Test
    fun getDirectionImageTest_invalidData() {
        assertThat(getDirectionImage(null)).isEqualTo(R.drawable.ic_routing_up)
        assertThat(getDirectionImage("")).isEqualTo(R.drawable.ic_routing_up)
        assertThat(getDirectionImage(" ")).isEqualTo(R.drawable.ic_routing_up)
        assertThat(getDirectionImage("null")).isEqualTo(R.drawable.ic_routing_up)
    }
}