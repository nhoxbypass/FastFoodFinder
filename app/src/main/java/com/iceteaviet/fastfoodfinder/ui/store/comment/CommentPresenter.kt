package com.iceteaviet.fastfoodfinder.ui.store.comment

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider

/**
 * Created by tom on 2019-04-18.
 */
class CommentPresenter : BasePresenter<CommentContract.Presenter>, CommentContract.Presenter {

    private val commentView: CommentContract.View

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, profileView: CommentContract.View) : super(dataManager, schedulerProvider) {
        this.commentView = profileView
    }

    override fun subscribe() {
    }

    override fun afterCommentTextChanged(text: CharSequence) {
        val length = text.length
        val remainChars = MAX_CHAR - length
        commentView.setRemainCharCountText(remainChars.toString())
        if (remainChars < 0) {
            commentView.updateTextColor(true)
        } else {
            commentView.updateTextColor(false)
        }

        commentView.setPostButtonEnabled(length > 0 && remainChars >= 0)
    }

    override fun onPostButtonClick(commentText: CharSequence) {
        if (commentText.isEmpty() || commentText.length > MAX_CHAR) {
            commentView.showCommentPostFailedWarning()
            return
        }

        val currUser = dataManager.getCurrentUser()
        if (currUser == null) {
            commentView.showGeneralErrorMessage()
            return
        }

        val comment = Comment(currUser.name, currUser.photoUrl,
                commentText.toString(), "", System.currentTimeMillis())
        commentView.exitWithResult(comment)
    }

    override fun onBackButtonClick(commentText: CharSequence) {
        if (commentText.isNotEmpty()) {
            commentView.showCloseConfirmDialog()
        } else {
            commentView.exit()
        }
    }

    companion object {
        const val MAX_CHAR = 140
    }
}