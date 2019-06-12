package com.iceteaviet.fastfoodfinder.utils

import android.app.Activity
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * Created by tom on 2019-06-12.
 */

class IntentUtilsTest {
    @Mock
    private lateinit var activity: Activity

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun openSplashActivityTest() {
        openSplashActivity(activity)

        verify(activity).startActivity(ArgumentMatchers.any())
    }

    @Test
    fun openLoginActivityTest() {
        openLoginActivity(activity)

        verify(activity).startActivity(ArgumentMatchers.any())
    }

    @Test
    fun openMainActivityTest() {
        openMainActivity(activity)

        verify(activity).startActivity(ArgumentMatchers.any())
    }

    @Test
    fun openARLiveSightActivityTest() {
        openARLiveSightActivity(activity)

        verify(activity).startActivity(ArgumentMatchers.any())
    }

    @Test
    fun openSettingsActivityTest() {
        openSettingsActivity(activity)

        verify(activity).startActivity(ArgumentMatchers.any())
    }

    @Test
    fun openRoutingActivityTest() {
        //openRoutingActivity(activity, store, mapsDirection)

        //verify(activity).startActivity(ArgumentMatchers.any())
    }

    @Test
    fun openStoreDetailActivityTest() {
        //openStoreDetailActivity(activity, store)

        //verify(activity).startActivity(ArgumentMatchers.any())
    }

    @Test
    fun openListDetailActivityTest() {
        //openListDetailActivity(activity, userstoreLists[0], "")

        //verify(activity).startActivity(ArgumentMatchers.any())
    }

    @Test
    fun openStoreListActivityTest() {
        openStoreListActivity(activity)

        verify(activity).startActivity(ArgumentMatchers.any())
    }

    @Test
    fun getNativeCallIntentTest() {
        //assertThat(getNativeCallIntent(TEL).dataString).isEqualTo("tel:$TEL")
        //assertThat(getNativeCallIntent("$TEL ").dataString).isEqualTo("tel:$TEL")
        //assertThat(getNativeCallIntent(" $TEL ").dataString).isEqualTo("tel:$TEL")
    }

    @Test
    fun makeNativeCallTest() {
        //makeNativeCall(activity, TEL)

        //verify(activity).startActivity(ArgumentMatchers.any())
    }

    @Test
    fun getImagePickerIntentTest() {
        //assertThat(getImagePickerIntent().action).isEqualTo(Intent.ACTION_PICK)
        //assertThat(getImagePickerIntent().data).isEqualTo(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    }

    companion object {
        private val TEL = "0968123456"

        private const val STORE_ID = 123
        private const val STORE_TITLE = "store_title"
        private const val STORE_ADDRESS = "store_address"
        private const val STORE_LAT = "10.773996"
        private const val STORE_LNG = "106.6898035"
        private const val STORE_TEL = "012345678965"

        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, StoreType.TYPE_CIRCLE_K)

        private val mapsDirection = MapsDirection()

        private val userstoreLists = getFakeUserStoreLists()
    }
}
