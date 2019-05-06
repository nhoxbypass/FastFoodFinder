package com.iceteaviet.fastfoodfinder.ui.store.comment

import android.os.Parcelable
import com.iceteaviet.fastfoodfinder.ui.base.BaseView

/**
 * Created by tom on 2019-04-18.
 */
interface CommentContract {
    interface View : BaseView<Presenter> {
        fun updateTextColor(overLimit: Boolean)
        fun setRemainCharCountText(remainCharCount: String)
        fun setPostButtonEnabled(enabled: Boolean)
        fun showCommentPostFailedWarning()
        fun exitWithResult(comment: Parcelable)
        fun exit()
        fun showCloseConfirmDialog()
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun afterCommentTextChanged(text: CharSequence, length: Int)
        fun onPostButtonClick(commentText: CharSequence)
        fun onBackButtonClick(commentText: CharSequence)
    }
}