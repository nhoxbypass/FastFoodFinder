package com.iceteaviet.fastfoodfinder.ui.storelist

import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import com.iceteaviet.fastfoodfinder.utils.getFakeStoreIds
import com.iceteaviet.fastfoodfinder.utils.getFakeStoreList
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

/**
 * Created by tom on 2019-06-17.
 */
class ListDetailPresenterTest {
    @Mock
    private lateinit var listDetailView: ListDetailContract.View

    private lateinit var listDetailPresenter: ListDetailPresenter

    @Mock
    private lateinit var dataManager: DataManager

    @Before
    fun setupPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        // Get a reference to the class under test
        listDetailPresenter = ListDetailPresenter(dataManager, TrampolineSchedulerProvider(), listDetailView)
    }

    @Test
    fun handleExtrasTest_nullUserStoreList() {
        listDetailPresenter.handleExtras(null, USER_PHOTO_URL)

        assertThat(listDetailPresenter.userStoreList).isEqualTo(UserStoreList())
        assertThat(listDetailPresenter.photoUrl).isEqualTo(USER_PHOTO_URL)
    }

    @Test
    fun handleExtrasTest_nullPhotoUrl() {
        listDetailPresenter.handleExtras(userStoreList, null)

        assertThat(listDetailPresenter.userStoreList).isEqualTo(userStoreList)
        assertThat(listDetailPresenter.photoUrl).isEqualTo("")
    }

    @Test
    fun handleExtrasTest_null() {
        listDetailPresenter.handleExtras(null, null)

        verify(listDetailView).exit()
    }

    @Test
    fun handleExtrasTest() {
        listDetailPresenter.handleExtras(userStoreList, USER_PHOTO_URL)

        assertThat(listDetailPresenter.userStoreList).isEqualTo(userStoreList)
        assertThat(listDetailPresenter.photoUrl).isEqualTo(USER_PHOTO_URL)
    }

    @Test
    fun subscribeTest_findStoresError() {
        // Preconditions
        listDetailPresenter.userStoreList = userStoreList
        listDetailPresenter.photoUrl = USER_PHOTO_URL
        `when`(dataManager.findStoresByIds(userStoreList.getStoreIdList())).thenReturn(Single.error(NotFoundException()))

        listDetailPresenter.subscribe()

        listDetailView.setListNameText(userStoreList.listName)
        listDetailView.loadStoreIcon(R.drawable.ic_profile_favourite)
        verify(listDetailView).showGeneralErrorMessage()
    }

    @Test
    fun subscribeTest() {
        // Preconditions
        listDetailPresenter.userStoreList = userStoreList
        listDetailPresenter.photoUrl = USER_PHOTO_URL
        `when`(dataManager.findStoresByIds(userStoreList.getStoreIdList())).thenReturn(Single.just(stores))

        listDetailPresenter.subscribe()

        listDetailView.setListNameText(userStoreList.listName)
        listDetailView.loadStoreIcon(R.drawable.ic_profile_favourite)
        verify(listDetailView).setStores(stores)
    }

    companion object {
        private const val USER_PHOTO_URL = "photourl.jpg"

        private val userStoreList = UserStoreList(1, getFakeStoreIds(), 2, "My Favourite Places")

        private val stores = getFakeStoreList()
    }
}