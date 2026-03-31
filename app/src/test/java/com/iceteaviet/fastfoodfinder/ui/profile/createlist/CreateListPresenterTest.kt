package com.iceteaviet.fastfoodfinder.ui.profile.createlist

import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class CreateListPresenterTest {
    private lateinit var mockAnnotations: AutoCloseable
    
    @Mock
    private lateinit var createListView : CreateListContract.View
    @Mock
    private lateinit var clientAuth: ClientAuth
    @Mock
    private lateinit var userRepository: UserRepository


    private lateinit var schedulerProvider: SchedulerProvider

    private lateinit var createListPresenter : CreateListPresenter

    @Before
    fun setupCreateListPresenter() {
        mockAnnotations = MockitoAnnotations.openMocks(this)

        schedulerProvider = TrampolineSchedulerProvider()

        createListPresenter = CreateListPresenter(clientAuth, userRepository, schedulerProvider, createListView)
    }

    @After
    fun tearDown() {
        mockAnnotations.close()
    }

    @Test
    fun onDoneButtonClickTest_EmptyName() {
        createListPresenter.onDoneButtonClick("")

        verify(createListView).showEmptyNameWarning()
    }

    @Test
    fun onDoneButtonClickTest_NotEmptyName() {
        createListPresenter.onDoneButtonClick("AmyTran")

        verify(createListView).notifyWithResult("AmyTran", createListPresenter.iconId)
    }

    @Test
    fun onCancelButtonClickTest(){
        createListPresenter.onCancelButtonClick()

        verify(createListView).cancel()
    }

    @Test
    fun onListIconSelectTest() {
        createListPresenter.onListIconSelect(1)

        assertThat(createListPresenter.iconId).isEqualTo(1)
        verify(createListView).updateSelectedIconUI(1)
    }
}