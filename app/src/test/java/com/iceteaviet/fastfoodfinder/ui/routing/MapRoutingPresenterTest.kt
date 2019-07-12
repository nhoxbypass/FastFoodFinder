package com.iceteaviet.fastfoodfinder.ui.routing

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.StoreType
import com.iceteaviet.fastfoodfinder.utils.getFakeEmptyLegMapsDirection
import com.iceteaviet.fastfoodfinder.utils.getFakeEmptyStepMapsDirection
import com.iceteaviet.fastfoodfinder.utils.getFakeMapsDirection
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyZeroInteractions
import org.mockito.MockitoAnnotations

/**
 * Created by tom on 2019-06-15.
 */
class MapRoutingPresenterTest {
    @Mock
    private lateinit var mapRoutingView: MapRoutingContract.View

    @Mock
    private lateinit var dataManager: DataManager

    private lateinit var mapRoutingPresenter: MapRoutingPresenter

    private lateinit var schedulerProvider: SchedulerProvider

    @Before
    fun setupPresenter() {
        MockitoAnnotations.initMocks(this)
        schedulerProvider = TrampolineSchedulerProvider()

        mapRoutingPresenter = MapRoutingPresenter(dataManager, schedulerProvider, mapRoutingView)
    }

    @Test
    fun handleExtrasTest_allNull() {
        mapRoutingPresenter.handleExtras(null, null)

        verify(mapRoutingView).exit()
        verify(mapRoutingView).showGetDirectionFailedMessage()
    }

    @Test
    fun handleExtrasTest_nullMapsDirection() {
        // Preconditions
        mapRoutingPresenter.handleExtras(null, store)

        mapRoutingPresenter.subscribe()

        verify(mapRoutingView).exit()
        verify(mapRoutingView).showGetDirectionFailedMessage()
    }

    @Test
    fun handleExtrasTest_invalidMapsDirection() {
        // Preconditions
        mapRoutingPresenter.handleExtras(invalidMapsDirection, store)

        mapRoutingPresenter.subscribe()

        verify(mapRoutingView).exit()
        verify(mapRoutingView).showGetDirectionFailedMessage()
    }

    @Test
    fun handleExtrasTest_emptyLegListMapsDirection() {
        // Preconditions
        val emptyLegListMapsDirection = getFakeEmptyLegMapsDirection()
        mapRoutingPresenter.handleExtras(emptyLegListMapsDirection, store)

        mapRoutingPresenter.subscribe()

        verify(mapRoutingView).exit()
        verify(mapRoutingView).showGetDirectionFailedMessage()
    }

    @Test
    fun handleExtrasTest_emptyStepListMapsDirection() {
        // Preconditions
        val emptyStepListMapsDirection = getFakeEmptyStepMapsDirection()
        mapRoutingPresenter.handleExtras(emptyStepListMapsDirection, store)

        mapRoutingPresenter.subscribe()

        verify(mapRoutingView).exit()
        verify(mapRoutingView).showGetDirectionFailedMessage()
    }

    @Test
    fun handleExtrasTest_nullStore() {
        // Preconditions
        mapRoutingPresenter.handleExtras(mapsDirection, null)

        mapRoutingPresenter.subscribe()

        verify(mapRoutingView).exit()
        verify(mapRoutingView).showGetDirectionFailedMessage()
    }

    @Test
    fun handleExtrasTest_invalidStore() {
        // Preconditions
        mapRoutingPresenter.handleExtras(mapsDirection, invalidStore)

        mapRoutingPresenter.subscribe()

        verify(mapRoutingView).exit()
        verify(mapRoutingView).showGetDirectionFailedMessage()
    }

    @Test
    fun handleExtrasTest_validData() {
        // Preconditions
        mapRoutingPresenter.handleExtras(mapsDirection, store)

        mapRoutingPresenter.subscribe()

        verify(mapRoutingView, never()).exit()
        verify(mapRoutingView, never()).showGetDirectionFailedMessage()
        assertThat(mapRoutingPresenter.currStore).isEqualTo(store)
        assertThat(mapRoutingPresenter.mapsDirection).isEqualTo(mapsDirection)
        assertThat(mapRoutingPresenter.stepList).isEqualTo(stepList)
        assertThat(mapRoutingPresenter.geoPointList).isEqualTo(geoPointList)
        verify(mapRoutingView).setStoreTitle(STORE_TITLE)
        verify(mapRoutingView).setRoutingStepList(stepList)
    }

    @Test
    fun subscribeTest() {
        mapRoutingPresenter.subscribe()
    }

    @Test
    fun onNavigationRowClickTest_negativeIndex() {
        // Preconditions
        mapRoutingPresenter.stepList = stepList
        mapRoutingPresenter.onNavigationRowClick(-1)

        verify(mapRoutingView).showGeneralErrorMessage()
        verify(mapRoutingView, never()).animateMapCamera(any(), ArgumentMatchers.anyBoolean())
        verify(mapRoutingView, never()).enterPreviewMode()
    }

    @Test
    fun onNavigationRowClickTest_bigIndex() {
        // Preconditions
        mapRoutingPresenter.stepList = stepList
        mapRoutingPresenter.onNavigationRowClick(stepList.size)

        verify(mapRoutingView).showGeneralErrorMessage()
        verify(mapRoutingView, never()).animateMapCamera(any(), ArgumentMatchers.anyBoolean())
        verify(mapRoutingView, never()).enterPreviewMode()
    }

    @Test
    fun onNavigationRowClickTest() {
        // Preconditions
        mapRoutingPresenter.stepList = stepList
        mapRoutingPresenter.onNavigationRowClick(1)

        verify(mapRoutingView).animateMapCamera(stepList[1].endMapCoordination.location, true)
        verify(mapRoutingView).enterPreviewMode()
        assertThat(mapRoutingPresenter.inPreviewMode).isTrue()
    }

    @Test
    fun onTopRoutingBannerPositionChangeTest_negativeIndex() {
        // Preconditions
        mapRoutingPresenter.stepList = stepList
        mapRoutingPresenter.onTopRoutingBannerPositionChange(-1)

        verifyZeroInteractions(mapRoutingView)
    }

    @Test
    fun onTopRoutingBannerPositionChangeTest_bigIndex() {
        // Preconditions
        mapRoutingPresenter.stepList = stepList
        mapRoutingPresenter.onTopRoutingBannerPositionChange(stepList.size)

        verifyZeroInteractions(mapRoutingView)
    }

    @Test
    fun onTopRoutingBannerPositionChangeTest_zeroIndex() {
        // Preconditions
        mapRoutingPresenter.stepList = stepList
        mapRoutingPresenter.onTopRoutingBannerPositionChange(0)

        verify(mapRoutingView).scrollTopBannerToPosition(0)
        verify(mapRoutingView).animateMapCamera(stepList[0].endMapCoordination.location, true)
    }

    @Test
    fun onTopRoutingBannerPositionChangeTest_normalIndex() {
        // Preconditions
        mapRoutingPresenter.stepList = stepList
        mapRoutingPresenter.onTopRoutingBannerPositionChange(2)

        verify(mapRoutingView).scrollTopBannerToPosition(2)
        verify(mapRoutingView).animateMapCamera(stepList[2].endMapCoordination.location, true)
    }

    @Test
    fun onPrevInstructionClickTest_negativeIndex() {
        // Preconditions
        mapRoutingPresenter.stepList = stepList
        mapRoutingPresenter.currDirectionIndex = 0

        mapRoutingPresenter.onPrevInstructionClick()

        assertThat(mapRoutingPresenter.currDirectionIndex).isEqualTo(stepList.size - 1) // Go back to the end coord
        verify(mapRoutingView).scrollTopBannerToPosition(ArgumentMatchers.anyInt())
        verify(mapRoutingView).animateMapCamera(any(), ArgumentMatchers.anyBoolean())
    }

    @Test
    fun onPrevInstructionClickTest() {
        // Preconditions
        mapRoutingPresenter.stepList = stepList
        mapRoutingPresenter.currDirectionIndex = 2

        mapRoutingPresenter.onPrevInstructionClick()

        assertThat(mapRoutingPresenter.currDirectionIndex).isEqualTo(1)
        verify(mapRoutingView).scrollTopBannerToPosition(1)
        verify(mapRoutingView).animateMapCamera(stepList[1].endMapCoordination.location, true)
    }

    @Test
    fun onNextInstructionClickTest_bigIndex() {
        // Preconditions
        mapRoutingPresenter.stepList = stepList
        mapRoutingPresenter.currDirectionIndex = stepList.size - 1

        mapRoutingPresenter.onNextInstructionClick()

        assertThat(mapRoutingPresenter.currDirectionIndex).isEqualTo(0) // Go back to the start coord
        verify(mapRoutingView).scrollTopBannerToPosition(ArgumentMatchers.anyInt())
        verify(mapRoutingView).animateMapCamera(any(), ArgumentMatchers.anyBoolean())
    }

    @Test
    fun onNextInstructionClickTest() {
        // Preconditions
        mapRoutingPresenter.stepList = stepList
        mapRoutingPresenter.currDirectionIndex = 1

        mapRoutingPresenter.onNextInstructionClick()

        assertThat(mapRoutingPresenter.currDirectionIndex).isEqualTo(2)
        verify(mapRoutingView).scrollTopBannerToPosition(2)
        verify(mapRoutingView).animateMapCamera(stepList[2].endMapCoordination.location, true)
    }

    @Test
    fun onBackArrowButtonPressTest_notInPreviewMode() {
        // Preconditions
        mapRoutingPresenter.inPreviewMode = false

        mapRoutingPresenter.onBackArrowButtonPress()

        verify(mapRoutingView).exit()
    }

    @Test
    fun onBackArrowButtonPressTest_inPreviewMode() {
        // Preconditions
        mapRoutingPresenter.inPreviewMode = true

        mapRoutingPresenter.onBackArrowButtonPress()

        verify(mapRoutingView).exitPreviewMode()
        verify(mapRoutingView, never()).exit()
        assertThat(mapRoutingPresenter.inPreviewMode).isFalse()
    }

    @Test
    fun onGetMapAsyncTest_noCurrentLocation() {
        // Preconditions
        mapRoutingPresenter.mapsDirection = mapsDirection
        mapRoutingPresenter.currStore = store
        mapRoutingPresenter.stepList = stepList
        mapRoutingPresenter.geoPointList = geoPointList
        mapRoutingPresenter.currLocation = null

        mapRoutingPresenter.onGetMapAsync()

        verify(mapRoutingView).addMapMarker(eq(LatLng(10.773996, 106.6898035)), eq(STORE_TITLE), eq(STORE_ADDRESS), ArgumentMatchers.anyInt())
        verify(mapRoutingView, never()).addMapMarker(any(), eq("Your location"), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())
        verify(mapRoutingView, never()).animateMapCamera(any(), ArgumentMatchers.anyBoolean())
        verify(mapRoutingView).drawRoutingPath(anyOrNull(), eq(geoPointList))
        verify(mapRoutingView).setTravelDurationText(mapsDirection.routeList[0].legList[0].getDuration())
        verify(mapRoutingView).setTravelDistanceText(mapsDirection.routeList[0].legList[0].getDistance())
        verify(mapRoutingView).setTravelSummaryText(String.format("Via %s", mapsDirection.routeList[0].summary))
    }

    @Test
    fun onGetMapAsyncTest() {
        // Preconditions
        mapRoutingPresenter.mapsDirection = mapsDirection
        mapRoutingPresenter.currStore = store
        mapRoutingPresenter.stepList = stepList
        mapRoutingPresenter.geoPointList = geoPointList
        mapRoutingPresenter.currLocation = location

        mapRoutingPresenter.onGetMapAsync()

        verify(mapRoutingView).addMapMarker(eq(LatLng(10.773996, 106.6898035)), eq(STORE_TITLE), eq(STORE_ADDRESS), ArgumentMatchers.anyInt())
        verify(mapRoutingView).addMapMarker(any(), eq("Your location"), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())
        verify(mapRoutingView).animateMapCamera(any(), ArgumentMatchers.anyBoolean())
        verify(mapRoutingView).drawRoutingPath(location, geoPointList)
        verify(mapRoutingView).setTravelDurationText(mapsDirection.routeList[0].legList[0].getDuration())
        verify(mapRoutingView).setTravelDistanceText(mapsDirection.routeList[0].legList[0].getDistance())
        verify(mapRoutingView).setTravelSummaryText(String.format("Via %s", mapsDirection.routeList[0].summary))
    }

    // Workaround solution
    private fun <T> anyObject(): T {
        return Mockito.anyObject<T>()
    }

    companion object {
        private const val STORE_ID = 123
        private const val STORE_TITLE = "store_title"
        private const val STORE_ADDRESS = "store_address"
        private const val STORE_LAT = "10.773996"
        private const val STORE_LNG = "106.6898035"
        private const val STORE_TEL = "012345678965"
        private const val STORE_TYPE = StoreType.TYPE_CIRCLE_K

        private const val STORE_INVALID_LAT = "0"
        private const val STORE_INVALID_LNG = "0"

        private val location = LatLng(10.1234, 106.1234)

        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE)
        val invalidStore = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_INVALID_LAT, STORE_INVALID_LNG, STORE_TEL, STORE_TYPE)

        private val invalidMapsDirection = MapsDirection()
        private val mapsDirection = getFakeMapsDirection()
        private val stepList = mapsDirection.routeList[0].legList[0].stepList
        private val geoPointList = PolyUtil.decode(mapsDirection.routeList[0].encodedPolylineString)
    }
}