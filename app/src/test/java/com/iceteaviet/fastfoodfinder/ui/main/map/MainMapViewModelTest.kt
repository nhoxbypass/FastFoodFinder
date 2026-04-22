package com.iceteaviet.fastfoodfinder.ui.main.map

import com.iceteaviet.fastfoodfinder.data.domain.prefs.PreferencesRepository
import com.iceteaviet.fastfoodfinder.data.domain.routing.MapsRoutingRepository
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.domain.model.Store
import com.iceteaviet.fastfoodfinder.ui.main.search.SearchEventBus
import com.iceteaviet.fastfoodfinder.ui.main.search.SearchEventResult
import com.iceteaviet.fastfoodfinder.utils.StoreType
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MainMapViewModelTest {

    private val storeRepository: StoreRepository = mock()
    private val mapsRoutingRepository: MapsRoutingRepository = mock()
    private val searchEventBus = SearchEventBus()
    private val preferencesRepository: PreferencesRepository = mock()

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testStore = Store(
        id = 1, title = "Circle K", address = "123 District 1",
        lat = 10.773996, lng = 106.6898035, tel = "0900000001", type = StoreType.TYPE_CIRCLE_K
    )

    private val testStore2 = Store(
        id = 2, title = "FamilyMart", address = "456 District 2",
        lat = 10.78, lng = 106.70, tel = "0900000002", type = StoreType.TYPE_FAMILY_MART
    )

    private lateinit var viewModel: MainMapViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MainMapViewModel(
            storeRepository, mapsRoutingRepository, searchEventBus, preferencesRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun start_emitsSetupMap() = runTest {
        whenever(storeRepository.getAllStores()).thenReturn(listOf(testStore))

        val events = mutableListOf<MainMapEvent>()
        val collectJob = launch(testDispatcher) { viewModel.uiEvent.collect { events.add(it) } }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.start(true)
        advanceUntilIdle()

        assertThat(events.any { it is MainMapEvent.SetupMap }).isTrue()
        collectJob.cancel()
    }

    @Test
    fun start_emitsRequestLocationPermission_whenNoPermission() = runTest {
        whenever(storeRepository.getAllStores()).thenReturn(listOf(testStore))

        val events = mutableListOf<MainMapEvent>()
        val collectJob = launch(testDispatcher) { viewModel.uiEvent.collect { events.add(it) } }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.start(false)
        advanceUntilIdle()

        assertThat(events.any { it is MainMapEvent.RequestLocationPermission }).isTrue()
        collectJob.cancel()
    }

    @Test
    fun start_emitsSetMyLocationEnabled_whenHasPermission() = runTest {
        whenever(storeRepository.getAllStores()).thenReturn(listOf(testStore))

        val events = mutableListOf<MainMapEvent>()
        val collectJob = launch(testDispatcher) { viewModel.uiEvent.collect { events.add(it) } }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.start(true)
        advanceUntilIdle()

        assertThat(events.any {
            it is MainMapEvent.SetMyLocationEnabled && it.enabled
        }).isTrue()
        collectJob.cancel()
    }

    @Test
    fun onNearByStoreClicked_emitsAnimateAndDialog() = runTest {
        whenever(storeRepository.getAllStores()).thenReturn(listOf(testStore))

        val events = mutableListOf<MainMapEvent>()
        val collectJob = launch(testDispatcher) { viewModel.uiEvent.collect { events.add(it) } }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.start(true)
        advanceUntilIdle()

        viewModel.onNearByStoreClicked(testStore)

        assertThat(events.any {
            it is MainMapEvent.AnimateMapCamera && it.zoomToDetail
        }).isTrue()
        assertThat(events.any { it is MainMapEvent.ShowDialogStoreInfo }).isTrue()
        collectJob.cancel()
    }

    @Test
    fun onCameraMoveStarted_emitsClearNearByStores() = runTest {
        whenever(storeRepository.getAllStores()).thenReturn(listOf(testStore))

        val events = mutableListOf<MainMapEvent>()
        val collectJob = launch(testDispatcher) { viewModel.uiEvent.collect { events.add(it) } }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.start(true)
        advanceUntilIdle()

        viewModel.onCameraMoveStarted()

        assertThat(events.any { it is MainMapEvent.ClearNearByStores }).isTrue()
        collectJob.cancel()
    }

    @Test
    fun onNavigationButtonClick_emitsInvalidStoreLocation_whenStoreLocationInvalid() = runTest {
        whenever(storeRepository.getAllStores()).thenReturn(listOf(testStore))
        val invalidStore = testStore.copy(lat = 0.0, lng = 0.0)

        val events = mutableListOf<MainMapEvent>()
        val collectJob = launch(testDispatcher) { viewModel.uiEvent.collect { events.add(it) } }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.start(true)
        advanceUntilIdle()

        viewModel.onNavigationButtonClick(invalidStore)

        assertThat(events.any { it is MainMapEvent.ShowInvalidStoreLocationWarning }).isTrue()
        collectJob.cancel()
    }

    @Test
    fun onNavigationButtonClick_emitsCannotGetLocation_whenCurrLocationNull() = runTest {
        whenever(storeRepository.getAllStores()).thenReturn(listOf(testStore))

        val events = mutableListOf<MainMapEvent>()
        val collectJob = launch(testDispatcher) { viewModel.uiEvent.collect { events.add(it) } }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.start(true)
        advanceUntilIdle()

        viewModel.onNavigationButtonClick(testStore)

        assertThat(events.any { it is MainMapEvent.ShowCannotGetLocationMessage }).isTrue()
        collectJob.cancel()
    }

    @Test
    fun onLocationFailed_fallsBackToPersistedLocation_whenAvailable() = runTest {
        whenever(storeRepository.getAllStores()).thenReturn(listOf(testStore))
        whenever(preferencesRepository.getLastKnownLocation()).thenReturn(Pair(10.5, 107.0))

        val events = mutableListOf<MainMapEvent>()
        val collectJob = launch(testDispatcher) { viewModel.uiEvent.collect { events.add(it) } }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.start(false)
        advanceUntilIdle()

        viewModel.onLocationFailed(-1)

        val animateEvent = events.filterIsInstance<MainMapEvent.AnimateMapCamera>().firstOrNull()
        assertThat(animateEvent).isNotNull()
        assertThat(animateEvent!!.location.latitude).isWithin(0.001).of(10.5)
        assertThat(animateEvent.location.longitude).isWithin(0.001).of(107.0)
        collectJob.cancel()
    }

    @Test
    fun onLocationFailed_fallsBackToDefault_whenNoPersistedLocation() = runTest {
        whenever(storeRepository.getAllStores()).thenReturn(listOf(testStore))
        whenever(preferencesRepository.getLastKnownLocation()).thenReturn(null)

        val events = mutableListOf<MainMapEvent>()
        val collectJob = launch(testDispatcher) { viewModel.uiEvent.collect { events.add(it) } }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.start(false)
        advanceUntilIdle()

        viewModel.onLocationFailed(-1)

        val animateEvent = events.filterIsInstance<MainMapEvent.AnimateMapCamera>().firstOrNull()
        assertThat(animateEvent).isNotNull()
        collectJob.cancel()
    }

    @Test
    fun start_loadsAllStoresToCluster() = runTest {
        whenever(storeRepository.getAllStores()).thenReturn(listOf(testStore, testStore2))

        val events = mutableListOf<MainMapEvent>()
        val collectJob = launch(testDispatcher) { viewModel.uiEvent.collect { events.add(it) } }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.start(true)
        advanceUntilIdle()

        val addEvent = events.filterIsInstance<MainMapEvent.AddStoresToCluster>().firstOrNull()
        assertThat(addEvent).isNotNull()
        assertThat(addEvent!!.stores).hasSize(2)
        collectJob.cancel()
    }

    @Test
    fun searchEvent_quickAction_loadsStoresByType() = runTest {
        whenever(storeRepository.getAllStores()).thenReturn(listOf(testStore))
        whenever(storeRepository.findStoresByType(StoreType.TYPE_CIRCLE_K)).thenReturn(listOf(testStore))

        val events = mutableListOf<MainMapEvent>()
        val collectJob = launch(testDispatcher) { viewModel.uiEvent.collect { events.add(it) } }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.start(true)
        advanceUntilIdle()

        searchEventBus.emit(SearchEventResult(SearchEventResult.SEARCH_ACTION_QUICK, StoreType.TYPE_CIRCLE_K))
        advanceUntilIdle()

        val addEvent = events.filterIsInstance<MainMapEvent.AddStoresToCluster>().lastOrNull()
        assertThat(addEvent).isNotNull()
        assertThat(addEvent!!.stores).hasSize(1)
        collectJob.cancel()
    }

    @Test
    fun searchEvent_storeClick_animatesAndShowsDialog() = runTest {
        whenever(storeRepository.getAllStores()).thenReturn(listOf(testStore))

        val events = mutableListOf<MainMapEvent>()
        val collectJob = launch(testDispatcher) { viewModel.uiEvent.collect { events.add(it) } }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.start(true)
        advanceUntilIdle()

        searchEventBus.emit(SearchEventResult(SearchEventResult.SEARCH_ACTION_STORE_CLICK, "q", testStore))
        advanceUntilIdle()

        assertThat(events.any {
            it is MainMapEvent.ShowDialogStoreInfo && it.store.id == 1
        }).isTrue()
        collectJob.cancel()
    }
}
