package com.iceteaviet.fastfoodfinder.ui.profile.cover

import android.graphics.drawable.Drawable
import com.iceteaviet.fastfoodfinder.ui.base.BaseView

/**
 * Created by tom on 2019-04-18.
 */
interface UpdateCoverContract {
    interface View : BaseView<Presenter> {
        fun openImageFilePicker()
        fun setSelectedImage(selectedImage: Drawable)
        fun dismissWithResult(selectedImage: Drawable?)
        fun cancel()
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun onImageBrowserButtonClick()
        fun onCoverImageSelect(selectedImage: Drawable)
        fun onDoneButtonClick()
    }
}