package com.iceteaviet.fastfoodfinder.ui.store.comment

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter

/**
 * Created by tom on 2019-04-18.
 */
class CommentPresenter : BasePresenter<CommentContract.Presenter>, CommentContract.Presenter {

    private val commentView: CommentContract.View

    constructor(dataManager: DataManager, profileView: CommentContract.View) : super(dataManager) {
        this.commentView = profileView
        this.commentView.presenter = this
    }

    override fun subscribe() {
    }

    override fun afterCommentTextChanged(text: CharSequence, length: Int) {
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
        if (commentText.length > MAX_CHAR) {
            commentView.showCommentPostFailedWarning()
            return
        }
        val comment = Comment(dataManager.getCurrentUser()!!.name, dataManager.getCurrentUser()!!.photoUrl,
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