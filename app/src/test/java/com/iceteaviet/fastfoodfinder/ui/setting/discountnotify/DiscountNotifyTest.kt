package com.iceteaviet.fastfoodfinder.ui.setting.discountnotify

import com.iceteaviet.fastfoodfinder.ui.settings.discountnotify.DiscountNotifyContract
import com.iceteaviet.fastfoodfinder.ui.settings.discountnotify.DiscountNotifyPresenter
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class DiscountNotifyTest {

    @Mock
    private lateinit var discountNotifyView : DiscountNotifyContract.View

    private lateinit var discountNotifyPresenter : DiscountNotifyPresenter

    @Before
    fun setupPresenter() {
        MockitoAnnotations.initMocks(this)

        discountNotifyPresenter = DiscountNotifyPresenter(discountNotifyView)
    }

    @Test
    fun onCancelButtonClickTest() {
        discountNotifyPresenter.onCancelButtonClick()

        verify(discountNotifyView).cancelDialog()
    }

    @Test
    fun onDoneButtonClickTest() {
        discountNotifyPresenter.onDoneButtonClick()

        verify(discountNotifyView).doneDialog()
    }

    @Test
    fun onSetupTagContainerTest() {
        discountNotifyPresenter.onSetupTagContainer()

        verify(discountNotifyView).setupTagContainer(LIST_STORES)
    }

    @Test
    fun getStoreNameTest() {
        var storeName = discountNotifyPresenter.getStoreName(KEY_MINI_STOP)

        assertThat(storeName).isEqualTo("Ministop")
    }

    companion object {
        private const val KEY_CIRCLE_K = "circle_k"
        private const val KEY_MINI_STOP = "mini_stop"
        private const val KEY_FAMILY_MART = "family_mark"
        private const val KEY_BSMART = "bsmart"
        private const val KEY_SHOP_N_GO = "shop_n_go"

        private val LIST_STORES = arrayOf(KEY_BSMART, KEY_CIRCLE_K, KEY_FAMILY_MART, KEY_MINI_STOP, KEY_SHOP_N_GO)
    }
}