package com.iceteaviet.fastfoodfinder.ui.splash

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * Created by tom on 2019-05-29.
 */
class SplashPresenterTest {

    @Mock
    private lateinit var splashView: SplashContract.View

    @Mock
    private lateinit var dataManager: DataManager

    private lateinit var splashPresenter: SplashPresenter

    private lateinit var schedulerProvider: SchedulerProvider

    @Before
    fun setupNotesPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        schedulerProvider = TrampolineSchedulerProvider()

        // Get a reference to the class under test
        splashPresenter = SplashPresenter(dataManager, schedulerProvider, splashView)
    }

    @Test
    fun subscribeTest() {
        splashPresenter.subscribe()
    }

    @Test
    fun loadStoreFromServerTest() {
        splashPresenter.loadStoresFromServer()
    }
}