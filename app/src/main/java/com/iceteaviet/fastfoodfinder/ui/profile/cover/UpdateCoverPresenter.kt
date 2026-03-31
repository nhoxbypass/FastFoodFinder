package com.iceteaviet.fastfoodfinder.ui.profile.cover

import android.graphics.drawable.Drawable
import androidx.annotation.VisibleForTesting
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider

/**
 * Created by tom on 2019-04-18.
 */
class UpdateCoverPresenter(
    private val clientAuth: com.iceteaviet.fastfoodfinder.data.auth.ClientAuth,
    private val userRepository: com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository,
    schedulerProvider: SchedulerProvider,
        private val updateCoverView: UpdateCoverContract.View
) : BasePresenter<UpdateCoverContract.Presenter>(schedulerProvider), UpdateCoverContract.Presenter {


    @VisibleForTesting
    var selectedImage: Drawable? = null

    

    override fun subscribe() {
    }

    override fun onImageBrowserButtonClick() {
        updateCoverView.openImageFilePicker()
    }

    override fun onCoverImageSelect(selectedImage: Drawable) {
        this.selectedImage = selectedImage

        updateCoverView.setSelectedImage(selectedImage)
    }

    override fun onDoneButtonClick() {
        updateCoverView.dismissWithResult(selectedImage)
    }
}