package com.iceteaviet.fastfoodfinder.ui.profile.cover

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class UpdateCoverPresenterTest {

    @Mock
    private lateinit var updateCoverView : UpdateCoverContract.View

    @Mock
    private lateinit var dataManager: DataManager

    private lateinit var schedulerProvider: SchedulerProvider

    private lateinit var updateCoverPresenter : UpdateCoverPresenter

    @Before
    fun setupPresenter() {
        MockitoAnnotations.initMocks(this)

        schedulerProvider = TrampolineSchedulerProvider()

        updateCoverPresenter = UpdateCoverPresenter(dataManager, schedulerProvider, updateCoverView)
    }

    @Test
    fun onImageBrowserButtonClickTest() {
        updateCoverPresenter.onImageBrowserButtonClick()

        verify(updateCoverView).openImageFilePicker()
    }

    @Test
    fun onCoverImageSelectTest() {
        var drawable = GradientDrawable()
        updateCoverPresenter.onCoverImageSelect(drawable)

        assertThat(updateCoverPresenter.selectedImage).isEqualTo(drawable)
        verify(updateCoverView).setSelectedImage(drawable)
    }

    @Test
    fun onDoneButtonClickTest() {
        var drawable = GradientDrawable()
        updateCoverPresenter.selectedImage = drawable

        updateCoverPresenter.onDoneButtonClick()

        verify(updateCoverView).dismissWithResult(drawable)
    }
}